package com.lucafaggion.thesis.develop.model;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
  @JsonIgnore
  private BigInteger id;

  private String run;
  private String name;
  private String uses;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  private RunnerJob job;
}
