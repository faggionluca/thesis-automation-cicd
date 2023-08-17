package com.lucafaggion.thesis.test.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;

import com.lucafaggion.thesis.develop.model.Repo;
import com.lucafaggion.thesis.develop.model.RepoEvent;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.lucafaggion.thesis.develop.repository.RepoEventRepository;
import com.lucafaggion.thesis.develop.repository.RepoRepository;
import com.lucafaggion.thesis.develop.repository.RunnerTaskConfigRepository;

import jakarta.transaction.Transactional;

public class RepoPushEventIntegrationTest extends ModelIntegrationFixtures {

  @Autowired
  RepoEventRepository repoEventRepository;
  @Autowired
  RepoRepository repoInitRepository;
  @Autowired
  RunnerTaskConfigRepository runnerTaskConfigRepository;
  
  private RepoPushEvent repoPushEvent;
  private Repo repo;

  @BeforeEach
  @Transactional
  @Commit
  void setUp() {
    this.repoPushEvent = RepoPushEvent.builder()
        .after("b36a7a92007de9702b694cba22f62ba1677f1f8a")
        .before("0000000000000000000000000000000000000000")
        .ref("refs/heads/test")
        .created(true)
        .deleted(false)
        .forced(false)
        .build();
    Repo tmp = Repo.builder()
        .events(new HashSet<RepoEvent>())
        .build();
    this.repo = repoInitRepository.save(tmp);
  }

  @Test
  @Commit
  void saveRepoPushEvent() {
    repoEventRepository.save(repoPushEvent);
    assertNotNull(repoPushEvent.getId(), "The RepoPushEvent should be saved");
  }

  @Test
  @Commit
  void saveRepoPushEventWithRepo() {
    // salviamo nel database dopo aver aggiunto l'evento ad un repository
    this.repo.addEvent(repoPushEvent);
    repoPushEvent = repoEventRepository.save(repoPushEvent);

    assertNotNull(repoPushEvent.getId(), "The repoPushEvent should be saved");
    assertTrue(this.repo.getEvents().size() > 0, "The events list should not be empty");
  }

  @Test
  @Commit
  void saveWithRunnerTaskConfig() {
    RunnerTaskConfig runnerTaskConfig = RunnerTaskConfig.builder()
        .name("testTask")
        .onEvent(Arrays.asList("push", "commit"))
        .build();
    
    this.repoPushEvent.addConfig(runnerTaskConfig);
    RepoPushEvent repoPushEventFromDb = repoEventRepository.save(repoPushEvent);

    assertNotNull(runnerTaskConfig.getId(), "runnerTaskConfig should be saved");
    assertNotNull(repoPushEventFromDb.getConfig(), "repoPushEventFromDb should have a reference to runnerTaskConfig");
  }
  
}
