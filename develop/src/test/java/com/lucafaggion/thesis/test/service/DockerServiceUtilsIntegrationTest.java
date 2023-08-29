package com.lucafaggion.thesis.test.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Mount;
import com.github.dockerjava.transport.DockerHttpClient;
import com.lucafaggion.thesis.develop.config.AppConfig;
import com.lucafaggion.thesis.develop.model.RunnerContext;
import com.lucafaggion.thesis.develop.service.ContainerService;
import com.lucafaggion.thesis.develop.service.ContextService;
import com.lucafaggion.thesis.develop.service.DockerContainerActionsService;
import com.lucafaggion.thesis.develop.service.DockerService;
import com.lucafaggion.thesis.develop.service.RunnerTaskConfigService;
import com.lucafaggion.thesis.develop.util.DockerServiceUtils;
import com.lucafaggion.thesis.test.UnitTestFixtures;

/**
 * DockerServiceInfoTest
 */
@Import(AppConfig.class)
@SpringBootTest(classes = { DockerContainerActionsService.class, RunnerTaskConfigService.class,
    SpringTemplateEngine.class, DockerService.class, ContextService.class })
public class DockerServiceUtilsIntegrationTest extends UnitTestFixtures {

  @Autowired
  ContextService context;

  @Autowired
  ContainerService<DockerClient, DockerHttpClient> docker;

  protected InspectContainerResponse containerInfo;

  @BeforeEach
  void setUpDockerServiceUtilsTest() throws InterruptedException {
    DockerClient client = docker.client();

    CreateContainerResponse container = client.createContainerCmd("gitcredhelper")
        .withCmd("test", "prova", "pwd", "https://test.test.test").exec();

    client.startContainerCmd(container.getId()).exec();
    WaitContainerResultCallback resultCallback = new WaitContainerResultCallback();
    client.waitContainerCmd(container.getId()).exec(resultCallback);
    containerInfo = client.inspectContainerCmd(container.getId()).exec();

    // Aggiungiamo al context i declared containers
    context.getContext().setVariable("output", Map.of("repository", "/repo"));

    resultCallback.awaitStatusCode();
  }

  @Test
  void shouldHaveAllDependecies() {
    assertNotNull(docker);
    assertNotNull(context);
  }

  @Test
  void shouldReturnCorrectList() throws InterruptedException {
    System.out.println(containerInfo.getMounts());
    List<Mount> mounts = DockerServiceUtils.mounts(containerInfo, context.getContext().getVariableAs("output"));
    System.out.println(mounts);
    assertNotNull(mounts);
  }

  @Test
  void shoulBeClonable() throws JsonMappingException, JsonProcessingException {
    List<Mount> mounts = DockerServiceUtils.mounts(containerInfo, context.getContext().getVariableAs("output"));

    String variableName = "mounts@internal";
    context.getContext().setVariable("mounts@internal", mounts);

    System.out.println(context.getContext().toTypedString());
    RunnerContext copyOfContext = context.getContext().copy();
    assertNotNull(copyOfContext.getVariable(variableName));
  }

  @AfterEach
  void cleanUpDockerServiceUtilsTest() {
    docker.client().removeContainerCmd(containerInfo.getId()).withRemoveVolumes(true).exec();
  }

}