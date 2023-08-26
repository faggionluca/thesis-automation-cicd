package com.lucafaggion.thesis.develop.service.exceptions;

public class RunnerJobStepExecutionException extends RuntimeException {
  
  public RunnerJobStepExecutionException() {
    super("Error during execution of step");
  }
}
