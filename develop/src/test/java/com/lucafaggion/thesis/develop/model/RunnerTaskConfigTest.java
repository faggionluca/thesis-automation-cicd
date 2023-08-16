package com.lucafaggion.thesis.develop.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RunnerTaskConfigTest extends RepoTest {

  protected RunnerTaskConfig runnerTaskConfig;

  @BeforeEach
  void setUpRunnerTaskConfig() throws IOException {
    // Carichiamo la config
    String config = ModelFixtures.loadConfig("runnerTaskConfig");
    // Testiamo la deserializzazione
    runnerTaskConfig = ModelFixtures.mapper.readValue(config, RunnerTaskConfig.class);
  }

  @Test
  void runnerTaskConfigDeserialize() {

    assertNotNull(runnerTaskConfig, "Deserialized object must not be null");
    assertTrue(runnerTaskConfig.getOnEvent().size() > 0, "getOn must have a size > 0 ");
    assertTrue(runnerTaskConfig.getJobs().size() > 0, "getJobs must have a size > 0 ");
    assertNull(runnerTaskConfig.getId(), "getId must be null");
    assertNotNull(runnerTaskConfig.getName(), "getName must be not null");
  }

}
