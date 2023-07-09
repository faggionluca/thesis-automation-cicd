package com.lucafaggion.thesis.develop.graph;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * RunnableGraph
 */
public class RunnableGraph {
  private Graph<String, DefaultEdge> graph;
  private Graph<ListenableFutureTask<String>, RunnableGraphEdge> runnableGraph;

  private static String GRAPH_FILE_LOCATION = "src/main/resources/graph.png";
  private ExecutorService taskExecutor;

  public RunnableGraph(ExecutorService executor) throws IOException {
    File imgFile = new File(GRAPH_FILE_LOCATION);
    this.taskExecutor = executor;
    imgFile.createNewFile();
  }

  public void createGraph() {
    
    Graph<ListenableFutureTask<String>, RunnableGraphEdge> future_g = new DirectedAcyclicGraph<>(
        RunnableGraphEdge.class);
    ListenableFutureTask<String> v1 = (new CallableTask<String>("task-v1", "v1")).getListenableFuture();
    ListenableFutureTask<String> v2 = (new CallableTask<String>("task-v2", "v2")).getListenableFuture();
    ListenableFutureTask<String> v3 = (new CallableTask<String>("task-v3", "v3")).getListenableFuture();
    ListenableFutureTask<String> v4 = (new CallableTask<String>("task-v4", "v4")).getListenableFuture();
    ListenableFutureTask<String> v5 = (new CallableTask<String>("task-v5", "v5")).getListenableFuture();

    Graph<String, DefaultEdge> g = new DirectedAcyclicGraph<>(DefaultEdge.class);
    g.addVertex("v1");
    g.addVertex("v2");
    g.addVertex("v3");
    g.addVertex("v4");
    g.addVertex("v5");
    g.addEdge("v1", "v2");
    g.addEdge("v2", "v4");
    g.addEdge("v2", "v3");
    g.addEdge("v4", "v5");
    g.addEdge("v3", "v5");
    this.graph = g;

    future_g.addVertex(v1);
    future_g.addVertex(v2);
    future_g.addVertex(v3);
    future_g.addVertex(v4);
    future_g.addVertex(v5);
    future_g.addEdge(v1, v2);
    future_g.addEdge(v2, v4);
    future_g.addEdge(v2, v3);
    future_g.addEdge(v4, v5);
    future_g.addEdge(v3, v5);
    this.runnableGraph = future_g;
  }

  public void performTraversal() {
    TopologicalOrderIterator<String, DefaultEdge> iterator = new TopologicalOrderIterator<String, DefaultEdge>(
        this.graph);
    while (iterator.hasNext()) {
      String current = iterator.next();
      System.out.println(current);
      System.out
          .println(String.format("vertex %s has incomingEdges: %s", current,
              this.graph.incomingEdgesOf(current).toString()));
    }
  }

  public void performRunnableTraversal() {
    TopologicalOrderIterator<ListenableFutureTask<String>, RunnableGraphEdge> iterator = new TopologicalOrderIterator<ListenableFutureTask<String>, RunnableGraphEdge>(
        this.runnableGraph);
    while (iterator.hasNext()) {
      ListenableFutureTask<String> current = iterator.next();
      Set<RunnableGraphEdge> incomingEdges = this.runnableGraph.incomingEdgesOf(current);
      if (incomingEdges.isEmpty()) {
        this.taskExecutor.submit(current);
      } else {
        ListenableFuture<List<String>> test = Futures
            .allAsList(incomingEdges.stream().map(edge -> edge.getSourcListenableFuture()).toList());
        Futures.addCallback(test, new FutureCallback<List<String>>() {
          @Override
          public void onSuccess(List<String> configResults) {
            System.out.println(configResults.toString());
            taskExecutor.submit(current);
          }

          @Override
          public void onFailure(Throwable t) {
            System.out.println("ERROR ON TASK");
          }
        }, taskExecutor);
      }

      // System.out.println(current);
      // System.out
      // .println(String.format("vertex %s has incomingEdges: %s", current,
      // this.runnableGraph.incomingEdgesOf(current).toString()));
    }
  }

  public void print() throws IOException {
    JGraphXAdapter<String, DefaultEdge> graphAdapter = new JGraphXAdapter<String, DefaultEdge>(this.graph);
    graphAdapter.getEdgeToCellMap().forEach((edge, cell) -> cell.setValue(null));
    mxIGraphLayout layout = new mxCompactTreeLayout(graphAdapter, false);
    layout.execute(graphAdapter.getDefaultParent());
    BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, new Color(0f, 0f, 0f, .5f), true,
        null);
    File imgFile = new File(GRAPH_FILE_LOCATION);
    ImageIO.write(image, "PNG", imgFile);
  }

}