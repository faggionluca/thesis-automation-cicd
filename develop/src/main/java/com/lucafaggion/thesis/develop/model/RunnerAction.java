package com.lucafaggion.thesis.develop.model;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.lucafaggion.thesis.develop.service.ContainerActionsService;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Setter(AccessLevel.NONE)
@Builder
public class RunnerAction implements Callable<String> {

  private final static Logger logger = LoggerFactory.getLogger(RunnerAction.class);

  @NonNull
  ContainerActionsService containerActionsService;
  
  @NonNull
  private RunnerJob job;

  @ToString.Exclude
  private ListenableFutureTask<String> listenableFuture;
  
  @NonNull
  @ToString.Exclude
  private RunnerContext context;

  @Override
  public String call() throws Exception {
    logger.debug("Executing RunnerAction with RunnerJob name: {}", job.getName());
    return containerActionsService.runActionInContainer(this);
  }
  
  public ListenableFutureTask<String> getListenableFuture() {
    if (this.listenableFuture != null)
      return this.listenableFuture;
    this.listenableFuture = ListenableFutureTask.create(this);
    return this.listenableFuture;
  }

}
