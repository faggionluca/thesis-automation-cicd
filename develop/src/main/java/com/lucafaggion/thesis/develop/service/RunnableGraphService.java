package com.lucafaggion.thesis.develop.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.lucafaggion.thesis.develop.graph.RunnableGraphEdge;
import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerContext;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;

@Service
@Scope("prototype")
public class RunnableGraphService {

  private final static Logger logger = LoggerFactory.getLogger(RunnableGraphService.class);

  @Autowired
  ContainerActionsService containerActionsService;

  public Graph<RunnerAction, RunnableGraphEdge> createAcyclicGraphFromConfig(RunnerTaskConfig runnerTaskConfig,
      RunnerContext context)
      throws JsonMappingException, JsonProcessingException {
    Graph<RunnerAction, RunnableGraphEdge> future_g = new DirectedAcyclicGraph<>(
        RunnableGraphEdge.class);

    // Create the array of callable jobs
    List<RunnerAction> actions = runnerTaskConfig.getJobs().values()
        .stream()
        .map((job) -> RunnerAction.builder()
            .containerActionsService(containerActionsService)
            .job(job)
            .context(context)
            .build())
        .collect(Collectors.toList());

    // Construct the acyclic graph
    // Fist lets add all the vertex
    for (RunnerAction runnerAction : actions) {
      future_g.addVertex(runnerAction);
    }

    // Creates all the directed vertex edges
    for (RunnerAction runnerAction : future_g.vertexSet()) {
      for (String dependsOn : runnerAction.getJob().getDependsOn()) {
        RunnerAction dependJob = future_g.vertexSet()
            .stream()
            .filter(vertex -> dependsOn.equals(vertex.getJob().getName()))
            .findFirst()
            .orElse(null);
        if (dependJob != null) {
          future_g.addEdge(dependJob, runnerAction);
        }
      }
    }

    return future_g;
  }

  private void addDebugCallbackForAction(RunnerAction action, Executor executor) {
    if (!logger.isDebugEnabled()) {
      return;
    }
    Futures.addCallback(action.getListenableFuture(), new FutureCallback<Object>() {
      @Override
      public void onSuccess(Object result) {
        // TODO: set complete on jobs
        logger.debug("[SUCCESS] finished executing RunnerAction name: {}", action.getJob().getName());
      }

      @Override
      public void onFailure(Throwable t) {
        // TODO: set error on jobs
        logger.debug("[ERROR] on RunnerAction name: {}", action.getJob().getName());
      }
    }, executor);
  }

  private void addDebugCallbackToLeafNode(RunnerAction current, ListenableFuture<List<String>> future,
      Executor executor) {
    if (!logger.isDebugEnabled()) {
      return;
    }
    Futures.addCallback(future, new FutureCallback<Object>() {
      @Override
      public void onSuccess(Object result) {
        // TODO: set complete on jobs
        logger.debug("[SUCCESS] result {}, before leaf node {}", result,
            current.getJob().getName());
      }

      @Override
      public void onFailure(Throwable t) {
        // TODO: set error on jobs
        logger.debug("[ERROR] on tasks before leaf node {}", current.getJob().getName());
      }
    }, executor);
  }

  public void executeGraph(Graph<RunnerAction, RunnableGraphEdge> graph, ExecutorService executorService)
      throws InterruptedException, ExecutionException {
    ListeningExecutorService service = MoreExecutors.listeningDecorator(executorService);
    // Aggiungi i callback per ogni runnertask
    graph.vertexSet().forEach(action -> addDebugCallbackForAction(action, service));

    List<ListenableFuture<?>> waitForAll = new ArrayList<>();

    // TODO: check executors ThreadPoolTaskExecutor
    TopologicalOrderIterator<RunnerAction, RunnableGraphEdge> iterator = new TopologicalOrderIterator<RunnerAction, RunnableGraphEdge>(
        graph);
    while (iterator.hasNext()) {
      RunnerAction current = iterator.next();
      ListenableFutureTask<String> currentFuture = current.getListenableFuture();
      Set<RunnableGraphEdge> incomingEdges = graph.incomingEdgesOf(current);
      Set<RunnableGraphEdge> outgoingEdges = graph.outgoingEdgesOf(current);

      if (incomingEdges.isEmpty()) {
        // questo nodo non dipende da nessun altro sono quindi nodi ROOT
        // ma altri nodi potrebbero dipendere da questo nodo
        waitForAll.add(service.submit(currentFuture));
      } else if (outgoingEdges.isEmpty()) {

        // qui siamo in un nodo finale (LEAF) ovver non abbiamo nodi che dipendono da
        // questo nodo
        // ma questo nodo dipende da altri nodi

        ListenableFutureTask<Object> leaf = ListenableFutureTask.create(() -> {
          ListenableFuture<List<String>> previusFutures = Futures
              .allAsList(incomingEdges.stream().map(edge -> edge.getSourceAction().getListenableFuture()).toList());
          addDebugCallbackToLeafNode(current, previusFutures, service);
          previusFutures.get();
          // TODO: merge the context
          ListenableFuture<?> leafTask = service.submit(currentFuture);
          leafTask.get();
          return null;
        });
        waitForAll.add(service.submit(leaf));
      } else {

        // qui siamo in un nodo dipendente sia da altri nodi sia esistono nodi
        // dipendenti da questo nodo

        ListenableFuture<List<String>> previusFutures = Futures
            .allAsList(incomingEdges.stream().map(edge -> edge.getSourceAction().getListenableFuture()).toList());
        Futures.addCallback(previusFutures, new FutureCallback<List<String>>() {
          @Override
          public void onSuccess(List<String> configResults) {
            logger.debug("[SUCCESS] result {}, starting task {}", configResults.toString(),
                current.getJob().getName());
            // TODO: merge the context
            waitForAll.add(service.submit(currentFuture));
          }

          @Override
          public void onFailure(Throwable t) {
            logger.debug("[ERROR] on one of tasks before {}", current.getJob().getName());
          }
        }, service);
        waitForAll.add(previusFutures);
      }
    }
    ListenableFuture<List<Object>> listFuture = Futures.allAsList(waitForAll);
    listFuture.get();
  }

  /*
   * Crea l'immagine di un qualsiasi grafico
   */
  public <T, E> void saveGraphToImage(Graph<T, E> graph, String location) throws IOException {
    JGraphXAdapter<T, E> graphAdapter = new JGraphXAdapter<T, E>(graph);
    graphAdapter.getEdgeToCellMap().forEach((edge, cell) -> cell.setValue(null));
    mxIGraphLayout layout = new mxCompactTreeLayout(graphAdapter, false);
    layout.execute(graphAdapter.getDefaultParent());
    BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, new Color(0f, 0f, 0f, .5f), true,
        null);
    File imgFile = new File(location);
    ImageIO.write(image, "PNG", imgFile);
  }

}
