package com.lucafaggion.thesis.test.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerJob;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.lucafaggion.thesis.develop.service.ContextService;
import com.lucafaggion.thesis.develop.service.DockerContainerActionsService;
import com.lucafaggion.thesis.develop.service.RunnerTaskConfigService;
import com.lucafaggion.thesis.test.UnitTestFixtures;

import lombok.Data;

// @Import(AppConfig.class)
// @SpringBootTest(classes = { RunnerTaskConfigService.class, ThreadPoolTaskExecutor.class,
//     SpringTemplateEngine.class, ContextService.class, DockerContainerActionsService.class, DockerService.class })
public class DockerContainerActionServiceIntegrationTest extends ServiceIntegrationFixtures {

  @Value("classpath:resources/templates/default_docker_service.config.json")
  Resource serviceConfigTemplate;

  @Autowired
  DockerContainerActionsService containerActionsService;

  @Autowired
  protected RunnerTaskConfigService runnerTaskConfigService;

  @Autowired
  ContextService contextService;

  protected RunnerTaskConfig runnerTaskConfig;

  protected RunnerAction runnerAction;

  @Data
  protected class ContextTestObject {
    public final String name = "Luca";
    public final String username = "faggionluca";
  }

  @BeforeEach
  void setUpDockerContainerActionServiceIntegrationTest()
      throws JsonMappingException, JsonProcessingException, IOException {
    runnerTaskConfig = runnerTaskConfigService.from(UnitTestFixtures.loadConfig("runnerTaskConfig"));
    contextService.getContext().setVariable("user", new ContextTestObject());

    runnerAction = RunnerAction.builder()
        .containerActionsService(containerActionsService)
        .job((RunnerJob) runnerTaskConfig.getJobs().values().toArray()[0])
        .context(contextService.getContext())
        .build();
  }

  @Test
  void checkTestConfiguration() throws IOException {
    assertNotNull(containerActionsService);
    assertNotNull(contextService);
    assertNotNull(runnerAction);
    // String classpath = System.getProperty("java.class.path");
    // String[] classPathValues = classpath.split(File.pathSeparator);
    // System.out.println(Arrays.toString(classPathValues));
    InputStream is = new ClassPathResource("templates/default_docker_service.config.json").getInputStream();
    assertNotNull(is);
  }

  @Test
  void testCreationOfService() throws IOException, InterruptedException {
    String serviceId = containerActionsService.createService(runnerAction);

    assertNotNull(serviceId, "The service should be created");
    TimeUnit.MILLISECONDS.sleep(5000);
    containerActionsService.shutDownService(serviceId);
  }

}
