package com.lucafaggion.thesis.develop.service;

import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerContext;

public interface ContainerActionsService {
  public RunnerContext runActionInContainer(RunnerAction action);
}
