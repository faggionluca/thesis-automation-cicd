package com.lucafaggion.thesis.develop.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.Test;

// Unit Tests per il POJO RunnerJobTest

public class RunnerJobTest extends ModelFixtures {

  @Test
  void runnerJobDeserialize() throws IOException {
    // Carichiamo la config
    String config = ModelFixtures.loadConfig("runnerJobOnlySteps");
    
    // Testiamo la deserializzazione
    RunnerJob runnerJobOnlySteps = ModelFixtures.mapper.readValue(config, RunnerJob.class);

    assertNotNull(runnerJobOnlySteps, "Deserialized object must not be null");
    assertNotNull(runnerJobOnlySteps.getSteps(), "getSteps must not be null");
    assertNull(runnerJobOnlySteps.getId(), "getId must be null");
    assertNull(runnerJobOnlySteps.getName(), "getName must be null");
    assertEquals(Collections.emptyList(), runnerJobOnlySteps.getDependsOn(), "getDependsOn must be empty");
  }

  @Test
  void runnerJobDeserializeAndSerialize() throws IOException {
    // Carichiamo la config
    String config = ModelFixtures.loadConfig("runnerJobOnlySteps");
    
    // Testiamo la deserializzazione
    RunnerJob runnerJobOnlySteps = ModelFixtures.mapper.readValue(config, RunnerJob.class);

    // Testiamo la serializzazione e deserializzazione
    String serializedrunnerJobOnlySteps = ModelFixtures.mapper.writeValueAsString(runnerJobOnlySteps);
    RunnerJob runnerJobOnlyStepsLoaded = ModelFixtures.mapper.readValue(serializedrunnerJobOnlySteps, RunnerJob.class);

    assertNotNull(runnerJobOnlyStepsLoaded, "Deserialized object must not be null");
    assertNotNull(runnerJobOnlyStepsLoaded, "Deserialized object must not be null");
    assertNotNull(runnerJobOnlyStepsLoaded.getSteps(), "getSteps must not be null");
    assertNull(runnerJobOnlyStepsLoaded.getId(), "getId must be null");
    assertNull(runnerJobOnlyStepsLoaded.getName(), "getName must be null");
    assertEquals(Collections.emptyList(), runnerJobOnlyStepsLoaded.getDependsOn(), "getDependsOn must be empty");
  }
}
