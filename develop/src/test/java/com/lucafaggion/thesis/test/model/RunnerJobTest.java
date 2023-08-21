package com.lucafaggion.thesis.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lucafaggion.thesis.develop.model.RunnerJob;
import com.lucafaggion.thesis.test.UnitTestFixtures;

// Unit Tests per il POJO RunnerJobTest

public class RunnerJobTest extends UnitTestFixtures {

  String config;
  RunnerJob runnerJobOnlySteps;

  @BeforeEach
  void SetUp() throws IOException {
    this.config = UnitTestFixtures.loadConfig("runnerJobOnlySteps");
    this.runnerJobOnlySteps = UnitTestFixtures.mapper.readValue(config, RunnerJob.class);
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
    String serializedrunnerJobOnlySteps = UnitTestFixtures.mapper.writeValueAsString(this.runnerJobOnlySteps);
    RunnerJob runnerJobOnlyStepsLoaded = UnitTestFixtures.mapper.readValue(serializedrunnerJobOnlySteps, RunnerJob.class);

    assertNotNull(runnerJobOnlyStepsLoaded, "Deserialized object must not be null");
    assertNotNull(runnerJobOnlyStepsLoaded.getSteps(), "getSteps must not be null");
    assertNull(runnerJobOnlyStepsLoaded.getId(), "getId must be null");
    assertNull(runnerJobOnlyStepsLoaded.getName(), "getName must be null");
    assertEquals(Collections.emptyList(), runnerJobOnlyStepsLoaded.getDependsOn(), "getDependsOn must be empty");
  }
}
