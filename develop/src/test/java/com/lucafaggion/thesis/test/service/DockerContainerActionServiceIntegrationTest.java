package com.lucafaggion.thesis.test.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.thymeleaf.TemplateEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Mount;
import com.github.dockerjava.api.model.MountType;
import com.github.dockerjava.api.model.ServiceSpec;
import com.github.dockerjava.api.model.TaskStatusContainerStatus;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.rholder.retry.RetryException;
import com.lucafaggion.thesis.develop.config.AppConfig;
import com.lucafaggion.thesis.develop.config.TemplateEngineConfig;
import com.lucafaggion.thesis.develop.config.converters.MountConverter;
import com.lucafaggion.thesis.develop.model.Repo;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerContext;
import com.lucafaggion.thesis.develop.model.RunnerJob;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.lucafaggion.thesis.develop.model.GitHub.GitHubPushEvent;
import com.lucafaggion.thesis.develop.service.ContainerService;
import com.lucafaggion.thesis.develop.service.ContextService;
import com.lucafaggion.thesis.develop.service.DockerContainerActionsService;
import com.lucafaggion.thesis.develop.service.DockerService;
import com.lucafaggion.thesis.develop.service.RunnerTaskConfigService;
import com.lucafaggion.thesis.develop.util.DockerServiceUtils;
import com.lucafaggion.thesis.test.UnitTestFixtures;

import lombok.Data;

@Import({ AppConfig.class, TemplateEngineConfig.class, MountConverter.class })
@SpringBootTest(classes = { RunnerTaskConfigService.class,
    ContextService.class,
    DockerContainerActionsService.class, DockerService.class })
@EnableAutoConfiguration(exclude = { JpaRepositoriesAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
    SecurityAutoConfiguration.class })
public class DockerContainerActionServiceIntegrationTest extends UnitTestFixtures {

  @Autowired
  ApplicationContext appContext;

  // @Autowired
  // ResourceLoader resourceLoader;

  @Autowired
  private DockerContainerActionsService containerActionsService;

  @Autowired
  protected RunnerTaskConfigService runnerTaskConfigService;

  @Autowired
  private ContextService contextService;

  @Autowired
  @Qualifier("yamlObjectMapper")
  private ObjectMapper mapper;

  @Autowired
  private ObjectMapper objectMapper;

  protected RunnerAction runnerAction;

  protected RunnerAction runnerActionSingle;

  @Autowired
  TemplateEngine templateEngine;

  @Autowired
  private ContainerService<DockerClient, DockerHttpClient> docker;

  private List<String> helperContainerList;

  private String serviceId;

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
    runnerTaskConfigSingleJob.setId(BigInteger.valueOf(1542));
    runnerTaskConfigSingleJob.setEvent(repoPushEvent);
    runnerTaskConfig.setId(BigInteger.valueOf(1542));
    runnerTaskConfig.setEvent(repoPushEvent);

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
    
    helperContainerList = new LinkedList<>();
  }

  @AfterEach
  void cleanUpDockerContainerActionServiceIntegrationTest() throws InterruptedException {
    helperContainerList.stream().forEach((containerId) -> {
      WaitContainerResultCallback callback = new WaitContainerResultCallback();
      docker.client().waitContainerCmd(containerId).exec(callback);
      try {
        callback.awaitCompletion();
        docker.client().removeContainerCmd(containerId).withRemoveVolumes(true).exec();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
    if (serviceId != null) {
      containerActionsService.shutDownService(serviceId);
      TimeUnit.MILLISECONDS.sleep(20000);
      ((List<Mount>) runnerAction.getContext().getVariableAs(ContextService.CONTAINER_MOUNTS)).stream()
          .forEach((mount) -> docker.client().removeVolumeCmd(mount.getSource()).exec());
    }
  }

  @Test
  void checkTestConfiguration() throws IOException {
    assertNotNull(containerActionsService);
    assertNotNull(contextService);
    assertNotNull(runnerAction);
  }

  @Test
  void testCreationOfService() throws IOException, InterruptedException {
    serviceId = containerActionsService.createService(runnerAction);

    assertNotNull(serviceId, "The service should be created");
    TimeUnit.MILLISECONDS.sleep(5000);
  }

  @Test
  void shouldBeAbleToRetriveTheContainer()
      throws ExecutionException, RetryException, IOException, InterruptedException {
    serviceId = containerActionsService.createService(runnerAction);
    TaskStatusContainerStatus containerStatus = containerActionsService.retriveContainerOfService(serviceId);

    assertNotNull(serviceId, "The service should be created");
    assertNotNull(containerStatus, "The containerStatus should be not null");

    TimeUnit.MILLISECONDS.sleep(5000);
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
  }

  @Test
  void compileACorrectDockerTemplate() throws IOException {
    List<Mount> mounts = List.of(new Mount().withTarget("/repo").withSource("source-vol").withType(MountType.VOLUME),
        new Mount().withTarget("/test").withSource("source-test").withType(MountType.VOLUME));

    ContextService.setVariablesOfContextFor(runnerAction);
    ContextService.addMountsToContext(runnerAction.getContext(), mounts);

    System.out.println(runnerAction.getContext().toString());
    System.out.println(runnerAction.getContext().getVariable(ContextService.CONTAINER_MOUNTS));

    String compiledTemplate = templateEngine.process("default_docker_service.config",
        runnerAction.getContext().toThymeleafContext(appContext));
    System.out.println(compiledTemplate);

    ServiceSpec spec = objectMapper.readValue(compiledTemplate, ServiceSpec.class);

    System.out.println(spec);

    assertNotNull(spec);
  }


  @Test
  void cloneRepo() throws IOException, InterruptedException {
    // String helperId = containerActionsService.cloneRepoUsingContainer(runnerAction);
    ContextService.setVariablesOfContextFor(runnerAction);

    runnerAction.getContext().setVariable(ContextService.REPO_USER, "faggionluca");
    runnerAction.getContext().setVariable(ContextService.REPO_TOKEN, "");

    System.out.println(runnerAction.getContext().getVariables());    

    System.out.println(DockerServiceUtils.getCloneCMD(runnerAction.getContext()).toString());
    // assertNotNull(helperId);

    String helperId = containerActionsService.cloneRepoUsingContainer(runnerAction);
    helperContainerList.add(helperId);
  }

  @Test
  void usingTheRepoVolume() throws Exception {
    ContextService.setVariablesOfContextFor(runnerAction);

    runnerAction.getContext().setVariable(ContextService.REPO_USER, "faggionluca");
    runnerAction.getContext().setVariable(ContextService.REPO_TOKEN, "");  

    String helperId = containerActionsService.cloneRepoUsingContainer(runnerAction);

    serviceId = containerActionsService.createService(runnerAction);
    TaskStatusContainerStatus containerStatus = containerActionsService.retriveContainerOfService(serviceId);

    if (containerStatus.getExitCodeLong() == null) {
      throw new NoSuchElementException("The container failed to start");
    }

    containerActionsService.execJob(containerStatus.getContainerID(), runnerAction.getJob(), runnerAction.getContext());

    helperContainerList.add(helperId);
    TimeUnit.MILLISECONDS.sleep(5000);
  }

  @Test
  void runActionInContainer() throws Exception {
  
    runnerAction.getContext().setVariable(ContextService.REPO_USER, "faggionluca");
    runnerAction.getContext().setVariable(ContextService.REPO_TOKEN, "");

    RunnerContext context = containerActionsService.runActionInContainer(runnerAction);
    
    System.out.println(context);
  }

  @Test
  void shouldCorrectlyCleanUp() {

    ContextService.setVariablesOfContextFor(runnerAction);

    DockerClient client = docker.client();
    List<String> mountNames = List.of("volume_1", "volume_2", "volume_3");
    mountNames.forEach((name) -> client.createVolumeCmd().withName(name).withLabels(ContextService.volumeLabelsFor(runnerAction.getContext())).exec());
    List<Mount> mounts = mountNames.stream().map((name) -> new Mount().withType(MountType.VOLUME).withTarget("/target").withSource(name)).collect(Collectors.toList());
    ContextService.addMountsToContext(runnerAction.getContext(), mounts);

    containerActionsService.cleanUp(runnerAction.getContext());
  }

}
