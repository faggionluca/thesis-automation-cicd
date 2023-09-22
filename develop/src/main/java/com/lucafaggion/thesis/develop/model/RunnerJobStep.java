package com.lucafaggion.thesis.develop.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lucafaggion.thesis.develop.util.ExceptionStatusUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
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

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(Include.NON_NULL)
@Table(name = "runner_job_step")
public class RunnerJobStep {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private BigInteger id;

  private List<String> run;
  private String name;
  private String uses;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "runner_job_id")
  private RunnerJob job;

  @JsonIgnore
  @OneToMany(
    mappedBy = "step",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @Builder.Default
  private List<RunnerStepLog> logs = new ArrayList<RunnerStepLog>();

  public void addLog(RunnerStepLog log) {
    this.logs.add(log);
    log.setStep(this);
  }

  @Embedded
  @Builder.Default
  private CurrentStatus status = ExceptionStatusUtils.defaultStatus();
}
