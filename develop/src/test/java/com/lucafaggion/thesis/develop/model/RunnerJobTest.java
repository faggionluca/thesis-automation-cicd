package com.lucafaggion.thesis.develop.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Unit Tests per il POJO RunnerJobTest

public class RunnerJobTest extends ModelFixtures {

  String config;
  RunnerJob runnerJobOnlySteps;

  @BeforeEach
  void SetUp() throws IOException {
    this.config = ModelFixtures.loadConfig("runnerJobOnlySteps");
    this.runnerJobOnlySteps = ModelFixtures.mapper.readValue(config, RunnerJob.class);
  }

  @Test
  void runnerJobDeserialize() throws IOException {

    assertNotNull(this.runnerJobOnlySteps, "Deserialized object must not be null");
    assertNotNull(this.runnerJobOnlySteps.getSteps(), "getSteps must not be null");
    assertNull(this.runnerJobOnlySteps.getId(), "getId must be null");
    assertNull(this.runnerJobOnlySteps.getName(), "getName must be null");
    assertEquals(Collections.emptyList(), this.runnerJobOnlySteps.getDependsOn(), "getDependsOn must be empty");
  }

  @Test
  void runnerJobDeserializeAndSerialize() throws IOException {
    // Testiamo la serializzazione e deserializzazione
    String serializedrunnerJobOnlySteps = ModelFixtures.mapper.writeValueAsString(this.runnerJobOnlySteps);
    RunnerJob runnerJobOnlyStepsLoaded = ModelFixtures.mapper.readValue(serializedrunnerJobOnlySteps, RunnerJob.class);

    assertNotNull(runnerJobOnlyStepsLoaded, "Deserialized object must not be null");
    assertNotNull(runnerJobOnlyStepsLoaded.getSteps(), "getSteps must not be null");
    assertNull(runnerJobOnlyStepsLoaded.getId(), "getId must be null");
    assertNull(runnerJobOnlyStepsLoaded.getName(), "getName must be null");
    assertEquals(Collections.emptyList(), runnerJobOnlyStepsLoaded.getDependsOn(), "getDependsOn must be empty");
  }
}
