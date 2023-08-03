package com.lucafaggion.thesis.develop.model;

import com.lucafaggion.thesis.common.model.User;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Setter(value = AccessLevel.PACKAGE)
@Getter
public class RepoPushEvent extends RepoEvent {
  private String ref;

  private String after;
  private String before;

  private boolean created;
  private boolean deleted;
  private boolean forced;

  @ManyToOne
  private User pusher;
}
