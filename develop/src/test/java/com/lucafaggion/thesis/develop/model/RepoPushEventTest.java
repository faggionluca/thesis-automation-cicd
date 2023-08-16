package com.lucafaggion.thesis.develop.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lucafaggion.thesis.develop.model.enums.Status;

public class RepoPushEventTest extends RunnerTaskConfigTest {

  protected RepoPushEvent repoPushEvent;

  @BeforeEach
  void setUpRepoPushEvent() {
    repoPushEvent = RepoPushEvent.builder()
        .ref("refs/heads/main")
        .after("dada2fab33e09b57f7317fd3b71dd7212f4d9d2f")
        .before("5e6fe1cbd442251123f6f4a941e8cbb8a57f3065")
        .created(true)
        .deleted(false)
        .forced(false)
        .pusher(BigInteger.valueOf(1))
        .config(runnerTaskConfig)
        .build();
    repo.addEvent(repoPushEvent);
  }

  @Test
  void checkRepoPushEventBuilder() {
    assertNotNull(repoPushEvent.getRepository(), "Repo should be set when adding the event to the repository");
    assertNotNull(repoPushEvent.getStatus(), "The status should be automatically set");
    assertTrue(repoPushEvent.getStatus().getStatus() == Status.CREATED, "The default status should be Status.CREATED");
  }

}
