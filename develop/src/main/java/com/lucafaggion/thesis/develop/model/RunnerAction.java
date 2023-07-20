package com.lucafaggion.thesis.develop.model;

import java.util.concurrent.Callable;

import com.google.common.util.concurrent.ListenableFutureTask;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class RunnerAction implements Callable<String> {

  @NonNull
  @Setter(AccessLevel.NONE)
  private RunnerJob job;

  @Setter(AccessLevel.NONE)
  @ToString.Exclude
  private ListenableFutureTask<String> listenableFuture;

  @Override
  public String call() throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'call'");
  }
  
  public ListenableFutureTask<String> getListenableFuture() {
    if (this.listenableFuture != null)
      return this.listenableFuture;
    this.listenableFuture = ListenableFutureTask.create(this);
    return this.listenableFuture;
  }

}
