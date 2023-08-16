package com.lucafaggion.thesis.develop.model;

import java.math.BigInteger;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;

public class RepoTest extends ModelFixtures {

  protected Repo repo;

  @BeforeEach
  void setUpRepoTest() {
    this.repo = Repo.builder()
        .id(BigInteger.valueOf(1))
        .name("webhook-events")
        .full_name("faggionluca/webhook-events")
        .isPrivate(true)
        .url("https://github.com/faggionluca/webhook-events.git")
        .owner(BigInteger.valueOf(1))
        .events(new HashSet<RepoEvent>())
        .build();
  }

}
