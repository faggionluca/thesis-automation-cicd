package com.lucafaggion.thesis.develop.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;

import com.lucafaggion.thesis.develop.repository.RunnerJobRepository;

public class RunnerJobIntergrationTest extends MondelIntegrationFixtures {
  
  @Autowired
  RunnerJobRepository runnerJobRepository;

  @Test
  @Commit // decommentare per debuggare i cambiamenti nel db
  void runnerJobSaveAndIsFoundInSet() {
    // Stiamo testando se la funzione hashCode non muta dopo il salvataggio
    Set<RunnerJob> set = new HashSet<>();

    RunnerJob testRunnerJobSave = RunnerJob.builder()
        .dependsOn(Arrays.asList("depends1", "depends2"))
        .name("just a job")
        .steps(null)
        .build();

    set.add(testRunnerJobSave);
    runnerJobRepository.save(testRunnerJobSave);

    assertTrue(set.contains(testRunnerJobSave), "Entity not found in the set");
  }
  

  @Test
  @Commit // decommentare per debuggare i cambiamenti nel db
  void runnerJobSaveFull() throws IOException {
    // Carichiamo la config e deserializziamo 
    String config = ModelFixtures.loadConfig("runnerJobOnlySteps");
    RunnerJob runnerJob = ModelFixtures.mapper.readValue(config, RunnerJob.class);
    
    // Salviamo su database
    runnerJobRepository.save(runnerJob);

    assertNotNull(runnerJob.getId(), "Entity is not saved to the database");
  }
}
