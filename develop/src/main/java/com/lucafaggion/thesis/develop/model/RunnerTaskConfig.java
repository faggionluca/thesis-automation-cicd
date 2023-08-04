package com.lucafaggion.thesis.develop.model;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(Include.NON_NULL)
@Table(name = "runner_task_config")
public class RunnerTaskConfig {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private BigInteger id;

  private String name;

  @JsonProperty("on")
  private List<String> onEvent;

  @OneToMany(
    mappedBy = "taskConfig",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @MapKey(name = "name")
  private Map<String, RunnerJob> jobs;

  @OneToOne
  @JoinColumn(name = "event_id")
  private RepoEvent event;

  @JsonProperty("jobs")
  public void setJobs(Map<String, RunnerJob> runnerJobs) {
    runnerJobs.values().stream().forEach(job -> job.setTaskConfig(this));
    this.jobs = runnerJobs;
  }

}
