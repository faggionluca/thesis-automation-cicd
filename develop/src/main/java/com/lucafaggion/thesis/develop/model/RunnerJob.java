package com.lucafaggion.thesis.develop.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "runner_job")
@Jacksonized
public class RunnerJob {

  @Id
  @JsonIgnore
  private BigInteger id;

  @JsonIgnore
  private String name;

  @JsonProperty("depends-on")
  private List<String> dependsOn;

  @ToString.Exclude
  @OneToMany(
    mappedBy = "job",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<RunnerJobStep> steps;

  public List<String> getDependsOn() {
    if (this.dependsOn == null) {
      this.dependsOn = new ArrayList<String>();
    }
    return this.dependsOn;
  }
}
