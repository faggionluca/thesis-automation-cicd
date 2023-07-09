package com.lucafaggion.thesis.develop.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RunnerJob {
  List<RunnerJobStep> steps;
}
