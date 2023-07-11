package com.lucafaggion.thesis.develop.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class RunnerTaskConfig {
  private String name;
  private List<String> on;
  private Map<String, RunnerJob> jobs;
}
