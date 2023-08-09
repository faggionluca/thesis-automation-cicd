package com.lucafaggion.thesis.develop.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;

import com.lucafaggion.thesis.develop.repository.RunnerTaskConfigRepository;

public class RunnerTaskConfigIntegrationTest extends ModelIntegrationFixtures {

  @Autowired
  RunnerTaskConfigRepository runnerTaskConfigRepository;

  @Test
  @Commit // decommentare per debuggare i cambiamenti nel db
  void runnerTaskConfigSaveAndIsFound() {
    // Stiamo testando se la funzione hashCode non muta dopo il salvataggio
    Set<RunnerTaskConfig> set = new HashSet<>();

    RunnerTaskConfig runnerTaskConfig = RunnerTaskConfig.builder()
        .name("testTask")
        .onEvent(Arrays.asList("push", "commit"))
        .build();

    set.add(runnerTaskConfig);
    runnerTaskConfigRepository.save(runnerTaskConfig);

    assertTrue(set.contains(runnerTaskConfig), "Entity not found in the set");
  }

  @Test
  @Commit // decommentare per debuggare i cambiamenti nel db
  void runnerTaskConfigSaveFull() throws IOException {
    // Carichiamo la config e deserializziamo
    String config = ModelFixtures.loadConfig("runnerTaskConfig");
    // RunnerTaskConfig runnerTaskConfig = RunnerTaskConfig.builder().build();
    RunnerTaskConfig runnerTaskConfig = ModelFixtures.mapper.readValue(config, RunnerTaskConfig.class);

    // Save
    runnerTaskConfigRepository.save(runnerTaskConfig);

    assertNotNull(runnerTaskConfig.getId(), "Entity is not saved to the database");
    assertFalse(runnerTaskConfig.getJobs().values().stream().anyMatch(job -> job.getTaskConfig().getId() == null),
        "Each Job should have a Task reference set");
    assertFalse(runnerTaskConfig.getJobs().values().stream().anyMatch(job -> job.getId() == null),
        "Each Job should be saved");
    assertFalse(runnerTaskConfig.getJobs().values().stream()
        .map(job -> job.getSteps().stream().map(step -> step.getId())).anyMatch(id -> id == null),
        "Each Job should save his Steps property");
    assertFalse(runnerTaskConfig.getJobs().values().stream()
        .map(job -> job.getSteps().stream().map(step -> step.getJob().getId())).anyMatch(id -> id == null),
        "Each Step should have a Job reference set");
  }
}
