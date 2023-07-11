package com.lucafaggion.thesis.develop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.util.concurrent.ListenableFutureTask;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import lombok.AccessLevel;

@Data
@Builder
@Jacksonized
public class RunnerJob implements Callable<String> {

  @JsonIgnore
  private String name;

  @JsonProperty("depends-on")
  private List<String> dependsOn;

  @ToString.Exclude
  private List<RunnerJobStep> steps;

  @JsonIgnore
  @Setter(AccessLevel.NONE)
  @ToString.Exclude
  private ListenableFutureTask<String> listenableFuture;

  @Override
  public String call() throws Exception {
    // TODO Auto-generated method stub
    return "Task depends-on: " + dependsOn.toString();
  }

  public List<String> getDependsOn() {
    if (this.dependsOn == null) {
      this.dependsOn = new ArrayList<String>();
    }
    return this.dependsOn;
  }

  public ListenableFutureTask<String> getListenableFuture() {
    if (this.listenableFuture != null)
      return this.listenableFuture;
    this.listenableFuture = ListenableFutureTask.create(this);
    return this.listenableFuture;
  }
}
