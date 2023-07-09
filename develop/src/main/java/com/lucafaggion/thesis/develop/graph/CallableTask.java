package com.lucafaggion.thesis.develop.graph;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

public class CallableTask<T> implements Callable<T> {

  private String name;
  private T result;

  public CallableTask(String name, T result) {
    this.name = name;
    this.result = result;
  }

  @Override
  public T call() throws Exception {
    int low = 5000;
    int high = 10000;
    System.out.println(String.format("Executing Task [%s]", name));
    TimeUnit.MILLISECONDS.sleep((new Random()).nextInt(high - low) + low);
    return result;
  }

  public ListenableFutureTask<T> getListenableFuture() {
    return ListenableFutureTask.create(this);
  }

}
