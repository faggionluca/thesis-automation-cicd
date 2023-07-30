package com.lucafaggion.thesis.develop.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;

import com.lucafaggion.thesis.develop.repository.RunnerJobRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
  "spring.datasource.url= jdbc:postgresql://postdb:5432/testrundb"
})
public class RunnerJobIntergrationTest {
  
  @Autowired
  RunnerJobRepository runnerJobRepository;

  @Test
  @Commit
  void runnerJobSaveAndIsFoundInSet() {
    // Stiamo testando se la funzione hashCode non muta dopo il salvataggio

    Set<RunnerJob> set = new HashSet<>();

    RunnerJob testRunnerJobSave = RunnerJob.builder()
        .dependsOn(Arrays.asList("depends1", "depends2"))
        .name("just a job")
        .steps(null)
        .id(BigInteger.ONE)
        .build();

    set.add(testRunnerJobSave);
    runnerJobRepository.save(testRunnerJobSave);

    assertTrue(set.contains(testRunnerJobSave), "Entity not found in the set");
  }
  
}
