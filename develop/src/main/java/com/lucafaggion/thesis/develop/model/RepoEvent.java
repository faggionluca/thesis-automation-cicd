package com.lucafaggion.thesis.develop.model;

import java.math.BigInteger;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
@SuperBuilder
@Table(name = "event")
public class RepoEvent {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private BigInteger id;

  @Nonnull
  private String type;

  @ManyToOne
  @JoinTable(name = "repo_event",
    joinColumns = @JoinColumn(name = "event", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "repo", referencedColumnName = "id")
  )
  private Repo repo;
}
