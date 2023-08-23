package com.lucafaggion.thesis.test.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jgrapht.Graph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lucafaggion.thesis.develop.config.AppConfig;
import com.lucafaggion.thesis.develop.graph.RunnableGraphEdge;
import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.lucafaggion.thesis.develop.service.ContainerActionsService;
import com.lucafaggion.thesis.develop.service.ContextService;
import com.lucafaggion.thesis.develop.service.RunnableGraphService;
import com.lucafaggion.thesis.develop.service.RunnerTaskConfigService;
import com.lucafaggion.thesis.test.UnitTestFixtures;

@Import(AppConfig.class)
@SpringBootTest(classes = { RunnableGraphService.class, RunnerTaskConfigService.class, ThreadPoolTaskExecutor.class,
    SpringTemplateEngine.class, ContextService.class })
public class RunnableGraphServiceIntegrationTest extends UnitTestFixtures {

  @Autowired
  protected RunnableGraphService runnableGraphService;

  @Autowired
  protected RunnerTaskConfigService runnerTaskConfigService;

  @Autowired
  ContextService contextService;

  protected ExecutorService taskExecutor;

  @MockBean
  ContainerActionsService containerActionsService;

  protected RunnerTaskConfig runnerTaskConfig;

  protected void waitRandomBetween(int low, int high) throws InterruptedException {
    TimeUnit.MILLISECONDS.sleep((new Random()).nextInt(high - low) + low);
  }

  @BeforeEach
  void setUpRunnableGraphServiceIntegrationTest() throws JsonMappingException, JsonProcessingException, IOException {
    runnerTaskConfig = runnerTaskConfigService.from(UnitTestFixtures.loadConfig("runnerTaskConfig"));
    contextService.getContext().setVariable("user", new Object() {
      public final String name = "Luca";
      public final String username = "faggionluca";
    });
    taskExecutor = Executors.newFixedThreadPool(20);
  }

  @Test
  void shouldCreateAValidGraph() throws JsonMappingException, JsonProcessingException {
    Graph<RunnerAction, RunnableGraphEdge> graph = runnableGraphService.createAcyclicGraphFromConfig(runnerTaskConfig,
        contextService.getContext());

    TopologicalOrderIterator<RunnerAction, RunnableGraphEdge> iterator = new TopologicalOrderIterator<RunnerAction, RunnableGraphEdge>(
        graph);
    while (iterator.hasNext()) {
      RunnerAction current = iterator.next();
      System.out.println(current);
      System.out.println(
          String.format("vertex %s has incomingEdges: %s", current, graph.incomingEdgesOf(current).toString()));
    }

    assertNotNull(graph, "RunnableGraphService should create a valid graph");
    assertFalse(graph.vertexSet().isEmpty(), "The graph should have a non empty vertexSet");
    assertFalse(graph.edgeSet().isEmpty(), "The graph should have a non empty edgeSet");
  }

  @Test
  void shouldExecuteTheGraph()
      throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException {
    List<String> executionExpectedResult = List.of("Explore-GitHub-Actions", "Dependent-Task1", "Dependent-Task2",
        "Dependent-Task3", "Final-task");
    List<String> executionExpectedResult_variant = List.of("Explore-GitHub-Actions", "Dependent-Task1",
        "Dependent-Task3",
        "Dependent-Task2", "Final-task");
    List<String> executionResult = new LinkedList<>();
    Mockito.when(containerActionsService.runActionInContainer(Mockito.any())).thenAnswer(new Answer<String>() {
      @Override
      public String answer(InvocationOnMock invocation) throws InterruptedException {
        RunnerAction runnerAction = (RunnerAction) invocation.getArgument(0);
        executionResult.add(runnerAction.getJob().getName());
        waitRandomBetween(2000, 3000);
        System.out.println("finished runActionInContainer of " + runnerAction.getJob().getName());
        return "ok";
      }
    });

    System.out.println("Creating and executing the graph....");
    Graph<RunnerAction, RunnableGraphEdge> graph = runnableGraphService.createAcyclicGraphFromConfig(runnerTaskConfig,
        contextService.getContext());
    runnableGraphService.executeGraph(graph, taskExecutor);

    System.out.println("Finished Executing graph");
    System.out.println(executionResult);
    assertTrue(
        executionExpectedResult.equals(executionResult) || executionExpectedResult_variant.equals(executionResult),
        "Execution should be orderer correctly");
  }

  @Test
  void shouldCorrectlyExecuteTheGraphWithErrors()
      throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException {
    Mockito.when(containerActionsService.runActionInContainer(Mockito.any())).thenAnswer(new Answer<String>() {
      @Override
      public String answer(InvocationOnMock invocation) throws InterruptedException {
        RunnerAction runnerAction = (RunnerAction) invocation.getArgument(0);
        // executionResult.add(runnerAction.getJob().getName());
        waitRandomBetween(2000, 3000);
        // solo questa runner action fallira!
        if (runnerAction.getJob().getName().equals("Dependent-Task3")) {
          System.out.println("trhowing ERROR on runActionInContainer of " + runnerAction.getJob().getName());
          throw new NoSuchElementException("error on task " + runnerAction.getJob().getName());
        }
        System.out.println("finished runActionInContainer of " + runnerAction.getJob().getName());
        return "ok";
      }
    });
    System.out.println("Creating and executing the graph....");
    Graph<RunnerAction, RunnableGraphEdge> graph = runnableGraphService.createAcyclicGraphFromConfig(runnerTaskConfig,
        contextService.getContext());
    runnableGraphService.executeGraph(graph, taskExecutor);

    System.out.println("Finished Executing graph");
    // System.out.println(executionResult);
  }

  @Test
  void beanShouldBeAutowired() {
    assertNotNull(runnableGraphService, "The RunnableGraphService should be autowirable");
  }
}
