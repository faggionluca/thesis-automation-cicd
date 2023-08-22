package com.lucafaggion.thesis.develop.graph;

import org.jgrapht.graph.DefaultEdge;

import com.google.common.util.concurrent.ListenableFuture;
import com.lucafaggion.thesis.develop.model.RunnerAction;

/**
 * RunnableGraphEdge
 */
public class RunnableGraphEdge extends DefaultEdge {
  public ListenableFuture<String> getSourcListenableFuture() {
    return (ListenableFuture<String>) this.getSource();
  }

  public RunnerAction getSourceAction() {
    return (RunnerAction) this.getSource();
  }
}