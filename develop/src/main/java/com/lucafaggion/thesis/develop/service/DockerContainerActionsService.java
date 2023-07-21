package com.lucafaggion.thesis.develop.service;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.CreateServiceResponse;
import com.github.dockerjava.api.command.InspectServiceCmd;
import com.github.dockerjava.api.command.PingCmd;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.ServiceModeConfig;
import com.github.dockerjava.api.model.ServiceSpec;
import com.github.dockerjava.api.model.Task;
import com.github.dockerjava.api.model.TaskState;
import com.github.dockerjava.api.model.TaskStatusContainerStatus;
import com.github.dockerjava.core.command.AttachContainerResultCallback;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient.Request;
import com.github.dockerjava.transport.DockerHttpClient.Response;
import com.github.rholder.retry.AttemptTimeLimiter;
import com.github.rholder.retry.AttemptTimeLimiters;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerJob;

@Service
public class DockerContainerActionsService implements ContainerActionsService {

  @Autowired
  ContainerService<DockerClient, DockerHttpClient> docker;

  @Override
  public String runActionInContainer(RunnerAction action) {
    DockerClient client = docker.client();
    DockerHttpClient httpClient = docker.http();

    String serviceConfig = """
        {\"Name\":\"task01\",\"TaskTemplate\":{\"ContainerSpec\":{\"Image\":\"chentex/random-logger\",\"Args\":[\"100\",\"400\",\"100\"]},\"RestartPolicy\":{\"Condition\":\"none\",\"MaxAttempts\":0}},\"Mode\":{\"Replicated\":{\"Replicas\":1}}}
              """;
    ObjectMapper mapper = new ObjectMapper();
    try {
      ServiceSpec spec = mapper.readValue(serviceConfig, ServiceSpec.class);
      System.out.println(spec.toString());
      CreateServiceResponse serviceResponse = client.createServiceCmd(spec).exec();
      System.out.println(serviceResponse.toString());

      // List<Task> status = client.listTasksCmd().withServiceFilter(serviceResponse.getId()).exec();

      
      Callable<List<Task>> taskStatusCallable = new Callable<List<Task>>() {
        public List<Task> call() throws Exception {
          return client.listTasksCmd().withServiceFilter(serviceResponse.getId()).exec();
        }
      };

      Retryer<List<Task>> taskStatusRetryer = RetryerBuilder.<List<Task>>newBuilder()
          .retryIfResult(Predicates.<List<Task>>isNull())
          .retryIfResult(new Predicate<List<Task>>() {
            @Override
            public boolean apply(List<Task> input) {
              return input.isEmpty();
            }
          })
          .retryIfExceptionOfType(IOException.class)
          .retryIfRuntimeException()
          .withWaitStrategy(WaitStrategies.fibonacciWait())
          .withAttemptTimeLimiter(AttemptTimeLimiters.fixedTimeLimit(30, TimeUnit.SECONDS))
          // .withStopStrategy(StopStrategies.stopAfterAttempt(3))
          .build();

      List<Task> status = taskStatusRetryer.call(taskStatusCallable);

      Callable<TaskStatusContainerStatus> containerStatusCallable = new Callable<TaskStatusContainerStatus>() {
        public TaskStatusContainerStatus call() throws Exception {
          Request request = Request.builder()
              .method(Request.Method.GET)
              .path("/tasks/" + status.get(0).getId())
              .build();

          Response response = httpClient.execute(request);

          ObjectMapper noExcpMapper = new ObjectMapper();
          noExcpMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
          Task task = noExcpMapper.readValue(responseBody, Task.class);

          System.out.println(task.getStatus().getContainerStatus());
          return task.getStatus().getContainerStatus();
        }
      };

      Retryer<TaskStatusContainerStatus> containerStatusRetryer = RetryerBuilder.<TaskStatusContainerStatus>newBuilder()
          .retryIfResult(Predicates.<TaskStatusContainerStatus>isNull())
          .retryIfExceptionOfType(IOException.class)
          .retryIfRuntimeException()
          .withWaitStrategy(WaitStrategies.fibonacciWait())
          .withAttemptTimeLimiter(AttemptTimeLimiters.fixedTimeLimit(30, TimeUnit.SECONDS))
          // .withStopStrategy(StopStrategies.stopAfterAttempt(3))
          .build();

      TaskStatusContainerStatus containerStatus = containerStatusRetryer.call(containerStatusCallable);

      // TRYING TO GET LOGS
      // Dovra essere implementato in un servizio a parte che scrivera i log
      // sia su socket che su database
      ResultCallback.Adapter<Frame> logCallback = new ResultCallback.Adapter<Frame>() {
        @Override
        public void onNext(Frame logfFrame) {
          System.out.println(logfFrame.toString());
        }
      };
      client.attachContainerCmd(containerStatus.getContainerID())
          .withFollowStream(true)
          .withStdOut(true)
          .exec(logCallback);
      // List<Container> containers = client.listContainersCmd().exec();
      // Optional<Container> taskContainer = containers.stream().filter((container) ->
      // {
      // List<String> names = Arrays.asList(container.getNames());
      // System.out.println(names.toString());
      // return Arrays.asList(container.getNames()).contains("/task01");
      // }).findFirst();

      WaitContainerResultCallback resultCallback = new WaitContainerResultCallback();
      client.waitContainerCmd(containerStatus.getContainerID()).exec(resultCallback);
      resultCallback.awaitCompletion();
      System.out.println("Service Completed");

      // Finally remove the service
      client.removeServiceCmd(serviceResponse.getId()).exec();

    } catch (IOException | InterruptedException e) {
      // TODO Auto-generated catch block
      System.out.println(ExceptionUtils.getStackTrace(e));
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (RetryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return "";
  }

}
