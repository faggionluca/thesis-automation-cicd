package com.lucafaggion.thesis.develop.model;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.ManyToAny;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
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
  // @JoinColumn(name = "runner_task_config_id")
  private Map<String, RunnerJob> jobs;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  private Repo repo;
}
