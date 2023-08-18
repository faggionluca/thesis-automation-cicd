package com.lucafaggion.thesis.test.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

import com.lucafaggion.thesis.develop.model.Repo;
import com.lucafaggion.thesis.develop.model.RepoEvent;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.lucafaggion.thesis.develop.service.GitHub.GitHubAPIService;
import com.lucafaggion.thesis.develop.service.exceptions.ConfigurationNotFoundException;

public class GitHubAPIServiceTest extends ServiceFixtures {

  @Autowired
  GitHubAPIService apiService;

  @Autowired
  RabbitTemplate rabbitTemplate;

  protected Repo buildRepo(BigInteger repoUserId, Boolean isPrivate, String full_name) {
    return Repo.builder()
    .events(new HashSet<RepoEvent>())
    .owner(repoUserId)
    .full_name(full_name)
    .isPrivate(isPrivate)
    .build();
  }

  protected RepoPushEvent buildRepoPushEvent( Repo repo) {
    return RepoPushEvent.builder()
        .after("720e4835dff4d74ae9694e086e9d9bc08f1b12f1")
        .before("0000000000000000000000000000000000000000")
        .ref("refs/heads/test")
        .repository(repo)
        .created(true)
        .deleted(false)
        .forced(false)
        .build();
  }

  @Test
  void testRetriveConfigNotFound() {
    // Prepariamo i Dati 
    Repo repo = buildRepo(BigInteger.valueOf(1), false, "not_found/not_found");
    RepoPushEvent event = buildRepoPushEvent(repo);
    repo.addEvent(event);

    assertThrows(ConfigurationNotFoundException.class, () -> apiService.retriveConfig(event));
  }

  @Test
  void testRetriveConfigFound() throws HttpClientErrorException, IOException {
    Repo repo = buildRepo(BigInteger.valueOf(1), false, "faggionluca/webhook-events");
    RepoPushEvent event = buildRepoPushEvent(repo);
    repo.addEvent(event);

    RunnerTaskConfig config = apiService.retriveConfig(event);
    assertNotNull(config, "Config should not be null");
  }

  @Test 
  void testRetriveConfigWithAuth() throws HttpClientErrorException, IOException {
    Repo repo = buildRepo(BigInteger.valueOf(1), true, "faggionluca/webhook-events");
    RepoPushEvent event = buildRepoPushEvent(repo);
    repo.addEvent(event);

    assertThrows(HttpClientErrorException.Unauthorized.class, () -> apiService.retriveConfig(event));
  }
}
