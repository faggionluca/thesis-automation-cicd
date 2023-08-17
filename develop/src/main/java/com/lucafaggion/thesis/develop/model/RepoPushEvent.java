package com.lucafaggion.thesis.develop.model;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lucafaggion.thesis.common.model.User;
import com.lucafaggion.thesis.develop.util.ExceptionStatusUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
public class RepoPushEvent extends RepoEvent {
  private String ref;

  private String after;
  private String before;

  private boolean created;
  private boolean deleted;
  private boolean forced;

  @Column(name = "pusher_id")
  private BigInteger pusher;

  @OneToOne(mappedBy = "event", cascade = CascadeType.ALL)
  @MapsId
  @PrimaryKeyJoinColumn
  @JsonIgnore
  private RunnerTaskConfig config;

  public void addConfig(RunnerTaskConfig config) {
    this.config = config;
    config.setEvent(this);
  }

  @Embedded
  @Builder.Default
  private CurrentStatus status = ExceptionStatusUtils.defaultStatus();
}
