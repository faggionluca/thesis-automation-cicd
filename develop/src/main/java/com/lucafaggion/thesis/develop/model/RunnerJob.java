package com.lucafaggion.thesis.develop.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "runner_job")
// @Jacksonized
@JsonInclude(Include.NON_NULL)
public class RunnerJob {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private BigInteger id;

  @JsonIgnore
  private String name;

  @JsonProperty("depends-on")
  @Builder.Default
  private List<String> dependsOn = new ArrayList<String>();

  @JsonProperty("run-on")
  @Builder.Default
  private String runOn = "alpine";

  @ToString.Exclude
  @OneToMany(
    mappedBy = "job",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<RunnerJobStep> steps;

  @JsonProperty("steps")
  public void setSteps(List<RunnerJobStep> runnerJobSteps) {
    runnerJobSteps.stream().forEach(step -> step.setJob(this));
    this.steps = runnerJobSteps;
  }

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "runner_task_config_id")
  private RunnerTaskConfig taskConfig;
}
