package com.lucafaggion.thesis.develop.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RunnerTaskConfig {
  private String name;
  private List<String> on;
  private Map<String, RunnerJob> jobs;
}
