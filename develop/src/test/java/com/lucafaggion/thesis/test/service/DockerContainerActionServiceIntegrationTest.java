package com.lucafaggion.thesis.test.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.model.TaskStatusContainerStatus;
import com.github.rholder.retry.RetryException;
import com.lucafaggion.thesis.develop.config.AppConfig;
import com.lucafaggion.thesis.develop.config.TemplateEngineConfig;
import com.lucafaggion.thesis.develop.model.Repo;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerJob;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.lucafaggion.thesis.develop.model.GitHub.GitHubPushEvent;
import com.lucafaggion.thesis.develop.service.ContextService;
import com.lucafaggion.thesis.develop.service.DockerContainerActionsService;
import com.lucafaggion.thesis.develop.service.DockerService;
import com.lucafaggion.thesis.develop.service.RunnerTaskConfigService;
import com.lucafaggion.thesis.test.UnitTestFixtures;

import lombok.Data;

@Import({ AppConfig.class, TemplateEngineConfig.class })
@SpringBootTest(classes = { RunnerTaskConfigService.class, SpringTemplateEngine.class, ContextService.class,
    DockerContainerActionsService.class, DockerService.class })
public class DockerContainerActionServiceIntegrationTest extends UnitTestFixtures {

  @Autowired
  DockerContainerActionsService containerActionsService;

  @Autowired
  protected RunnerTaskConfigService runnerTaskConfigService;

  @Autowired
  ContextService contextService;

  @Autowired
  @Qualifier("yamlObjectMapper")
  private ObjectMapper mapper;

  @Autowired
  private ObjectMapper objectMapper;

  protected RunnerAction runnerAction;

  protected RunnerAction runnerActionSingle;

  @Data
  protected class ContextTestObject {
    public final String name = "Luca";
    public final String username = "faggionluca";
  }

  @BeforeEach
  void setUpDockerContainerActionServiceIntegrationTest()
      throws JsonMappingException, JsonProcessingException, IOException {

    GitHubPushEvent gitHubPushEvent = objectMapper.readValue(UnitTestFixtures.loadConfig("GH_repoPushEvent"),
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

    RunnerTaskConfig runnerTaskConfig = runnerTaskConfigService.from(UnitTestFixtures.loadConfig("runnerTaskConfig"));
    RunnerTaskConfig runnerTaskConfigSingleJob = runnerTaskConfigService
        .from(UnitTestFixtures.loadConfig("runnerTaskConfigSingleJob"));
    runnerTaskConfigSingleJob.setEvent(repoPushEvent);

    
    contextService.getContext().setVariable("user", new ContextTestObject());

    runnerAction = RunnerAction.builder()
        .containerActionsService(containerActionsService)
        .job((RunnerJob) runnerTaskConfig.getJobs().values().toArray()[0])
        .context(contextService.getContext())
        .build();

    runnerActionSingle = RunnerAction.builder()
        .containerActionsService(containerActionsService)
        .job((RunnerJob) runnerTaskConfigSingleJob.getJobs().values().toArray()[0])
        .context(contextService.getContext())
        .build();
  }

  @Test
  void checkTestConfiguration() throws IOException {
    assertNotNull(containerActionsService);
    assertNotNull(contextService);
    assertNotNull(runnerAction);
  }

  @Test
  void testCreationOfService() throws IOException, InterruptedException {
    String serviceId = containerActionsService.createService(runnerAction);

    assertNotNull(serviceId, "The service should be created");
    TimeUnit.MILLISECONDS.sleep(5000);
    containerActionsService.shutDownService(serviceId);
  }

  @Test
  void shouldBeAbleToRetriveTheContainer()
      throws ExecutionException, RetryException, IOException, InterruptedException {
    String serviceId = containerActionsService.createService(runnerAction);
    TaskStatusContainerStatus containerStatus = containerActionsService.retriveContainerOfService(serviceId);

    assertNotNull(serviceId, "The service should be created");
    assertNotNull(containerStatus, "The containerStatus should be not null");

    TimeUnit.MILLISECONDS.sleep(5000);
    containerActionsService.shutDownService(serviceId);
  }

  @Test
  void shouldBeAlbleToExecuteATask() throws Exception {
    String serviceId = containerActionsService.createService(runnerActionSingle);
    TaskStatusContainerStatus containerStatus = containerActionsService.retriveContainerOfService(serviceId);

    try {
      containerActionsService.execJobTask(containerStatus.getContainerID(),
          runnerActionSingle.getJob().getSteps().get(0),
          contextService.getContext());
    } catch (Exception e) {
      containerActionsService.shutDownService(serviceId);
      throw e;
    }

    containerActionsService.shutDownService(serviceId);
  }

  @Test
  void shouldBeAlbleToExecuteAJob() throws Exception {
    String serviceId = containerActionsService.createService(runnerActionSingle);
    TaskStatusContainerStatus containerStatus = containerActionsService.retriveContainerOfService(serviceId);

    try {
      containerActionsService.execJob(containerStatus.getContainerID(), runnerActionSingle.getJob(),
          contextService.getContext());
    } catch (Exception e) {
      containerActionsService.shutDownService(serviceId);
      throw e;
    }

    containerActionsService.shutDownService(serviceId);
  }

}
