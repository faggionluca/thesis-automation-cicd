package com.lucafaggion.thesis.develop.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;

import com.lucafaggion.thesis.develop.repository.RepoEventRepository;
import com.lucafaggion.thesis.develop.repository.RepoRepository;

import jakarta.transaction.Transactional;

public class RepoPushEventIntegrationTest extends MondelIntegrationFixtures {

  @Autowired
  RepoEventRepository repoEventRepository;
  @Autowired
  RepoRepository repoInitRepository;
  
  private RepoPushEvent repoPushEvent;
  private Repo repo;

  @BeforeEach
  @Transactional
  @Commit
  void setUp() {
    this.repoPushEvent = RepoPushEvent.builder()
        .type("push")
        .after("b36a7a92007de9702b694cba22f62ba1677f1f8a")
        .before("0000000000000000000000000000000000000000")
        .ref("refs/heads/test")
        .created(true)
        .deleted(false)
        .forced(false)
        .build();
    this.repo = Repo.builder()
        .location("http:someurl.com/repo.git")
        .events(new HashSet<RepoEvent>())
        .build();
    repoInitRepository.save(repo);
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
    Repo dbRepo = this.repoInitRepository.getReferenceById((long) 1);
    dbRepo.addEvent(repoPushEvent);
    repoPushEvent = repoEventRepository.save(repoPushEvent);

    //
    Repo dbRepo1 = this.repoInitRepository.getReferenceById((long) 1);
    Set<RepoEvent> events = dbRepo1.getEvents();

    assertNotNull(repoPushEvent.getId(), "The repoPushEvent should be saved");
    assertTrue(events.size() > 0, "The events list should not be empty");
  }
}
