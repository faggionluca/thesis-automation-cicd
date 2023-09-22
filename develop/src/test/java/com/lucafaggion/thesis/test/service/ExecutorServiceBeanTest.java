package com.lucafaggion.thesis.test.service;

import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ExecutorServiceBeanTest extends ServiceIntegrationFixtures {
  
  @Autowired
  ExecutorService executorService;

  @Autowired
  @Qualifier("runnerExecutor")
  ExecutorService executorServiceQualifier;

  @Test
  void shouldAutowire() {
    System.out.println(executorService);
    System.out.println(executorServiceQualifier);

  }

}
