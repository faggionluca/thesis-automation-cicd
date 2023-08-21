package com.lucafaggion.thesis.test.model;

import java.math.BigInteger;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;

import com.lucafaggion.thesis.develop.model.Repo;
import com.lucafaggion.thesis.develop.model.RepoEvent;
import com.lucafaggion.thesis.test.UnitTestFixtures;

public class RepoTest extends UnitTestFixtures {

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
