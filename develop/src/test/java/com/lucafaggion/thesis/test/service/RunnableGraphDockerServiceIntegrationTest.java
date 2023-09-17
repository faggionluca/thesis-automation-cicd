package com.lucafaggion.thesis.test.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jgrapht.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucafaggion.thesis.develop.config.AppConfig;
import com.lucafaggion.thesis.develop.config.TemplateEngineConfig;
import com.lucafaggion.thesis.develop.config.converters.MountConverter;
import com.lucafaggion.thesis.develop.graph.RunnableGraphEdge;
import com.lucafaggion.thesis.develop.model.Repo;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.lucafaggion.thesis.develop.model.GitHub.GitHubPushEvent;
import com.lucafaggion.thesis.develop.service.ContainerActionsService;
import com.lucafaggion.thesis.develop.service.ContextService;
import com.lucafaggion.thesis.develop.service.DockerContainerActionsService;
import com.lucafaggion.thesis.develop.service.DockerService;
import com.lucafaggion.thesis.develop.service.RunnableGraphService;
import com.lucafaggion.thesis.develop.service.RunnerTaskConfigService;
import com.lucafaggion.thesis.test.UnitTestFixtures;

// @Import({ AppConfig.class, TemplateEngineConfig.class, MountConverter.class })
@SpringBootTest(classes = { AppConfig.class, TemplateEngineConfig.class, MountConverter.class,
    RunnerTaskConfigService.class, RunnableGraphService.class, RunnerTaskConfigService.class,
    ThreadPoolTaskExecutor.class, ContextService.class,
    DockerContainerActionsService.class, DockerService.class })
@EnableAutoConfiguration(exclude = { JpaRepositoriesAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
    SecurityAutoConfiguration.class })
public class RunnableGraphDockerServiceIntegrationTest extends UnitTestFixtures {

  @Autowired
  protected RunnableGraphService runnableGraphService;

  @Autowired
  protected RunnerTaskConfigService runnerTaskConfigService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  protected ContextService contextService;

  protected ExecutorService taskExecutor;

  @Autowired
  protected ContainerActionsService containerActionsService;

  protected RunnerTaskConfig runnerTaskConfig;

  @BeforeEach
  void setUpRunnableGraphServiceIntegrationTest() throws JsonMappingException, JsonProcessingException, IOException {

    GitHubPushEvent gitHubPushEvent = objectMapper.readValue(UnitTestFixtures.loadConfig("GH_springTestPushEvent"),
        GitHubPushEvent.class);

    Repo repo = Repo.builder().full_name(gitHubPushEvent.getRepository().getFull_name())
        .name(gitHubPushEvent.getRepository().getName()).url(gitHubPushEvent.getRepository().getUrl()).build();

    RepoPushEvent repoPushEvent = RepoPushEvent.builder()
        .ref(gitHubPushEvent.getRef())
        .repository(repo)
        .after(gitHubPushEvent.getAfter())
        .before(gitHubPushEvent.getBefore())
        .created(gitHubPushEvent.isCreated())
        .deleted(gitHubPushEvent.isDeleted())
        .forced(gitHubPushEvent.isForced())
        .build();

    runnerTaskConfig = runnerTaskConfigService.from(UnitTestFixtures.loadConfig("exampleSpringTest"));
    runnerTaskConfig.setId(BigInteger.valueOf(1542));
    runnerTaskConfig.setEvent(repoPushEvent);

    taskExecutor = Executors.newFixedThreadPool(20);
  }

  @Test
  void executeFullRun() throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException {
    Graph<RunnerAction, RunnableGraphEdge> graph = runnableGraphService.createAcyclicGraphFromConfig(runnerTaskConfig,
        contextService.getContext());
    runnableGraphService.executeGraph(graph, taskExecutor);
  }

}
