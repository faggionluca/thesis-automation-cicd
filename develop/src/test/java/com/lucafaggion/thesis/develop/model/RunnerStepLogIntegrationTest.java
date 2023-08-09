package com.lucafaggion.thesis.develop.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Commit;

public class RunnerStepLogIntegrationTest extends RunnerJobIntergrationTest {
  
  List<RunnerStepLog> logs;

  @BeforeEach
  void setUpRunnerStepLog() {
    RunnerStepLog log1 = RunnerStepLog.builder().value("i'm the 1st row").build();
    RunnerStepLog log2 = RunnerStepLog.builder().value("i'm the 2nd row").build();
    RunnerStepLog log3 = RunnerStepLog.builder().value("i'm the 3rd row").build();
    RunnerStepLog log4 = RunnerStepLog.builder().value("i'm the 4th row").build();
    RunnerStepLog log5 = RunnerStepLog.builder().value("i'm the 5th row").build();
    List<RunnerStepLog> tmp = new ArrayList<RunnerStepLog>();
    tmp.add(log1);
    tmp.add(log2);
    tmp.add(log3);
    tmp.add(log4);
    tmp.add(log5);
    this.logs = Collections.unmodifiableList(tmp);
  }

  @Test
  @Commit
  void saveAStepLog() {
    // Impostiamo la reference allo step per ogni log
    RunnerJobStep step = this.runnerJob.getSteps().get(0);
    for (RunnerStepLog log : this.logs) {
      step.addLog(log);
    }

    // salviamo il job
    this.runnerJobRepository.save(step.getJob());

    // ogni log deve essere salvato
    assertFalse(step.getLogs().stream().anyMatch(log -> log.getId() == null), "Logs must be saved");
  }

}
