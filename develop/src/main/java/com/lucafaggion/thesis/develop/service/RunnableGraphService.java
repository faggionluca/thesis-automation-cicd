package com.lucafaggion.thesis.develop.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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

  public void executeGraph(Graph<RunnerAction, RunnableGraphEdge> graph,
      ThreadPoolTaskExecutor threadPoolTaskExecutor) {

  }

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
