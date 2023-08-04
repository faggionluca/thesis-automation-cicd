package com.lucafaggion.thesis.develop.model;

import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


@Entity
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
@Table(name = "repository")
public class Repo {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private BigInteger id;

  private String node_id;
  private String name;
  private String full_name;

  private String url;

  @JsonAlias("private")
  private Boolean isPrivate;
  private String location;

  @OneToMany(
    mappedBy = "repository",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private Set<RepoEvent> events;

  public void addEvent(RepoEvent event) {
    this.events.add(event);
    event.setRepository(this);
  }
}
