package com.lucafaggion.thesis.develop.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class RunnerJob {

  @JsonIgnore
  private String name;

  @JsonProperty("depends-on")
  private List<String> dependsOn;

  @ToString.Exclude
  private List<RunnerJobStep> steps;

  public List<String> getDependsOn() {
    if (this.dependsOn == null) {
      this.dependsOn = new ArrayList<String>();
    }
    return this.dependsOn;
  }
}
