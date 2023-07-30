package com.lucafaggion.thesis.develop.model;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Builder
@Component
public class RunnerAction implements Callable<String> {

  @Autowired
  ContainerActionsService containerActionsService;
  
  @Setter(AccessLevel.NONE)
  @NonNull
  private RunnerJob job;

  @Setter(AccessLevel.NONE)
  @NonNull
  @ToString.Exclude
  private ListenableFutureTask<String> listenableFuture;

  @Override
  public String call() throws Exception {
    // TODO Auto-generated method stub
    return containerActionsService.runActionInContainer(this);
  }
  
  public ListenableFutureTask<String> getListenableFuture() {
    if (this.listenableFuture != null)
      return this.listenableFuture;
    this.listenableFuture = ListenableFutureTask.create(this);
    return this.listenableFuture;
  }

}
