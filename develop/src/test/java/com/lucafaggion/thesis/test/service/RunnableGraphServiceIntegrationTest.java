package com.lucafaggion.thesis.test.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.jgrapht.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lucafaggion.thesis.develop.graph.RunnableGraphEdge;
import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.lucafaggion.thesis.develop.service.ContainerActionsService;
import com.lucafaggion.thesis.develop.service.ContextService;
import com.lucafaggion.thesis.develop.service.RunnableGraphService;
import com.lucafaggion.thesis.develop.service.RunnerTaskConfigService;
import com.lucafaggion.thesis.test.UnitTestFixtures;

public class RunnableGraphServiceIntegrationTest extends ServiceIntegrationFixtures {

  @Autowired
  protected RunnableGraphService runnableGraphService;

  @Autowired
  protected RunnerTaskConfigService runnerTaskConfigService;

  @Autowired
  ContextService contextService;

  @MockBean
  ContainerActionsService containerActionsService;

  protected RunnerTaskConfig runnerTaskConfig;

  @BeforeEach
  void setUpRunnableGraphServiceIntegrationTest() throws JsonMappingException, JsonProcessingException, IOException {
    runnerTaskConfig = runnerTaskConfigService.from(UnitTestFixtures.loadConfig("runnerTaskConfig"));
    contextService.getContext().setVariable("user", new Object() {
      public final String name = "Luca";
      public final String username = "faggionluca";
    });
  }

  @Test
  void shouldCreateAValidGraph() throws JsonMappingException, JsonProcessingException {
    Graph<RunnerAction, RunnableGraphEdge> graph = runnableGraphService.createAcyclicGraphFromConfig(runnerTaskConfig, contextService.getContext());

    assertNotNull(graph, "RunnableGraphService should create a valid graph");
    assertFalse(graph.vertexSet().isEmpty(), "The graph should have a non empty vertexSet");
  }

  @Test
  void shouldExecuteTheGraph()  throws JsonMappingException, JsonProcessingException {
    Graph<RunnerAction, RunnableGraphEdge> graph = runnableGraphService.createAcyclicGraphFromConfig(runnerTaskConfig, contextService.getContext());

  }

  @Test
  void beanShouldBeAutowired() {
    assertNotNull(runnableGraphService, "The RunnableGraphService should be autowirable");
  }
}
