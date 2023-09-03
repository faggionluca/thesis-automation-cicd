package com.lucafaggion.thesis.develop.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.async.ResultCallback.Adapter;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.CreateServiceResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Mount;
import com.github.dockerjava.api.model.ServiceSpec;
import com.github.dockerjava.api.model.Task;
import com.github.dockerjava.api.model.TaskStatusContainerStatus;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient.Request;
import com.github.dockerjava.transport.DockerHttpClient.Response;
import com.github.rholder.retry.AttemptTimeLimiters;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerContext;
import com.lucafaggion.thesis.develop.model.RunnerJob;
import com.lucafaggion.thesis.develop.model.RunnerJobStep;
import com.lucafaggion.thesis.develop.service.exceptions.RunnerJobStepExecutionException;
import com.lucafaggion.thesis.develop.util.DockerServiceUtils;

@Service
public class DockerContainerActionsService implements ContainerActionsService {

  private final static Logger logger = LoggerFactory.getLogger(DockerContainerActionsService.class);

  @Autowired
  ApplicationContext appContext;

  @Autowired
  ResourceLoader resourceLoader;

  @Autowired
  RunnerTaskConfigService runnerTaskConfigService;

  @Autowired
  ContainerService<DockerClient, DockerHttpClient> docker;

  @Autowired
  ObjectMapper mapper;

  public long execJobTask(String containerId, RunnerJobStep runnerJobStep, RunnerContext context)
      throws InterruptedException {

    if (runnerJobStep.getRun() == null) {
      return 0;
    }
    DockerClient client = docker.client();

    ResultCallback.Adapter<Frame> logCallback = new ResultCallback.Adapter<Frame>() {
      @Override
      public void onNext(Frame logfFrame) {
        // System.out.println(String.format("[%s] %s", logfFrame.getStreamType(), new
        // String(logfFrame.getPayload(), 8, logfFrame.getPayload().length
        // ,StandardCharsets.UTF_8)).trim());
        System.out.println(logfFrame.toString());
      }
    };

    List<String> cmd = new LinkedList<>();

    logger.debug("Running RunnerJobStep with template: {}", runnerJobStep.getRun());
    for (String run : runnerJobStep.getRun()) {
      cmd.add(runnerTaskConfigService.compile(run, context.toThymeleafContext()));
    }
    logger.debug("Running RunnerJobStep with compiled CMD: {}", cmd);

    ExecCreateCmdResponse execCmdId = client.execCreateCmd(containerId)
        .withCmd(cmd.stream().toArray(String[]::new))
        .withAttachStderr(true)
        .withAttachStdin(true)
        .withAttachStdout(true)
        .withPrivileged(true)
        .withUser("root")
        .withTty(false)
        .exec();
    Adapter<Frame> runExecCmd = client.execStartCmd(execCmdId.getId()).exec(logCallback);
    runExecCmd.awaitCompletion();
    InspectExecResponse result = client.inspectExecCmd(execCmdId.getId()).exec();

    logger.debug("RunnerJobStep result: {}", result.getExitCodeLong());
    if (result.getExitCodeLong() != 0) {
      throw new RunnerJobStepExecutionException();
    }

    return result.getExitCodeLong();
  }

  public void execJob(String containerId, RunnerJob runnerJob, RunnerContext context) throws Exception {
    try {
      for (RunnerJobStep step : runnerJob.getSteps()) {
        long result = execJobTask(containerId, step, context);
        logger.debug("RunnerJobStep result: {}", result);
      }
    } catch (Exception e) {
      // TODO: handle exception an save ERORRS
      throw e;
    }
  }

  /**
   * Clone the specified RunnerAction repository 
   * @param action
   */
  public String cloneRepoUsingContainer(RunnerAction action) {
    DockerClient client = docker.client();

    // ---- CREIAMO ED ESEGUIAMO IL CONTAINER --------------------------
    CreateContainerResponse container = client.createContainerCmd("gitcredhelper")
        .withCmd(DockerServiceUtils.getCloneCMD(action.getContext()))
        .exec();

    client.startContainerCmd(container.getId()).exec();
    WaitContainerResultCallback resultCallback = new WaitContainerResultCallback();
    client.waitContainerCmd(container.getId()).exec(resultCallback);
    InspectContainerResponse containerInfo = client.inspectContainerCmd(container.getId()).exec();

    // ---- AGGIORNIAMO IL CONTEXT CON LE MOUNTS ------------------------
    List<Mount> mounts = DockerServiceUtils.mounts(containerInfo, Map.of("repository", "/repo"));
    ContextService.addMountsToContext(action.getContext(), mounts);

    return containerInfo.getId();
  }

  @Override
  public RunnerContext runActionInContainer(RunnerAction action) {

    // ---- AGGIORNIAMO IL CONTEXT DELL'ACTION --------------------------
    ContextService.setVariablesOfContextFor(action);
    // TODO: ContexService.updateGlobalMounts
    return null;
  }

  public void shutDownService(String serviceId) {
    DockerClient client = docker.client();
    logger.debug("Removing Docker service with ID: {}", serviceId);
    client.removeServiceCmd(serviceId).exec();
  }

  public String createService(RunnerAction action) throws IOException {
    DockerClient client = docker.client();

    // ---- COMPILIAMO IL TEMPLATE DEL DOCKER SERVICE --------------------------
    // Resource serviceConfigTemplate = resourceLoader
    // .getResource("classpath:templates/default_docker_service.config.json");
    // String serviceConfig = new
    // String(Files.readAllBytes(serviceConfigTemplate.getFile().toPath()));
    // logger.debug("Loaded Docker service config template from {} with value: {}",
    // serviceConfigTemplate.getFile().toPath(), serviceConfig);

    // // ---- CREA IL DOCKER SERVICE --------------------------
    // String compiledConfig = runnerTaskConfigService.compile(serviceConfig,
    // action.getContext().toThymeleafContext());
    String compiledConfig = runnerTaskConfigService.process("default_docker_service.config",
        action.getContext().toThymeleafContext(appContext));
    logger.debug("Compiled Docker service config template: {}", compiledConfig);
    ServiceSpec spec = mapper.readValue(compiledConfig, ServiceSpec.class);
    CreateServiceResponse serviceResponse = client.createServiceCmd(spec).exec();

    return serviceResponse.getId();
  }

  /*
   * Crea un Docker Swarm service task container
   */
  public TaskStatusContainerStatus retriveContainerOfService(String serviceId)
      throws ExecutionException, RetryException, IOException {

    DockerClient client = docker.client();
    DockerHttpClient httpClient = docker.http();

    // ---- RECUPERA LA SINGOLA TASK DEL DOCKER SERVICE --------------------------
    Callable<List<Task>> taskStatusCallable = new Callable<List<Task>>() {
      public List<Task> call() throws Exception {
        return client.listTasksCmd().withServiceFilter(serviceId).exec();
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

    // ---- RECUPERA IL CONTAINER ASSOCIATO ALLA TASK --------------------------
    Callable<TaskStatusContainerStatus> containerStatusCallable = new Callable<TaskStatusContainerStatus>() {
      public TaskStatusContainerStatus call() throws Exception {
        Request request = Request.builder()
            .method(Request.Method.GET)
            .path("/tasks/" + status.get(0).getId())
            .build();

        Response response = httpClient.execute(request);
        String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = mapper.readValue(responseBody, Task.class);
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
    logger.debug("Retrived container from task with: {}", containerStatus);

    return containerStatus;
  }

  private RunnerContext runActionInContainer_debug(RunnerAction action) {
    DockerClient client = docker.client();
    DockerHttpClient httpClient = docker.http();

    // TODO: Extract logic to service!
    // String serviceConfig = """
    // {\"Name\":\"task01\",\"TaskTemplate\":{\"ContainerSpec\":{\"Image\":\"chentex/random-logger\",\"Args\":[\"100\",\"400\",\"100\"]},\"RestartPolicy\":{\"Condition\":\"none\",\"MaxAttempts\":0}},\"Mode\":{\"Replicated\":{\"Replicas\":1}}}
    // """;
    String serviceConfig = """
        {\"Name\":\"task01\",\"TaskTemplate\":{\"ContainerSpec\":{\"Image\":\"alpine\",\"TTY\":true,\"OpenStdin\":true},\"RestartPolicy\":{\"Condition\":\"none\",\"MaxAttempts\":0}},\"Mode\":{\"Replicated\":{\"Replicas\":1}}}
            """;

    ObjectMapper mapper = new ObjectMapper();
    try {
      ServiceSpec spec = mapper.readValue(serviceConfig, ServiceSpec.class);
      System.out.println(spec.toString());
      CreateServiceResponse serviceResponse = client.createServiceCmd(spec).exec();
      System.out.println(serviceResponse.toString());

      // List<Task> status =
      // client.listTasksCmd().withServiceFilter(serviceResponse.getId()).exec();

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

      // IS the service still running?
      WaitContainerResultCallback resultCallback = new WaitContainerResultCallback();
      client.waitContainerCmd(containerStatus.getContainerID()).exec(resultCallback);

      // TRYING TO GET LOGS
      // Dovra essere implementato in un servizio a parte che scrivera i log
      // sia su socket che su database
      ResultCallback.Adapter<Frame> logCallback = new ResultCallback.Adapter<Frame>() {
        @Override
        public void onNext(Frame logfFrame) {
          System.out.println(logfFrame.toString());
        }
      };
      // client.attachContainerCmd(containerStatus.getContainerID())
      // .withFollowStream(true)
      // .withStdOut(true)
      // .exec(logCallback);

      // String initialString = "echo 'Hello World'";
      // InputStream sInputStream = new
      // ReaderInputStream(CharSource.wrap(initialString).openStream(),
      // Charsets.UTF_8);

      // Lets try to get a exec to work
      ExecCreateCmdResponse execCmdId = client.execCreateCmd(containerStatus.getContainerID())
          .withCmd("asdadadsa", "Hello World")
          .withAttachStderr(true)
          .withAttachStdin(true)
          .withAttachStdout(true)
          .withPrivileged(true)
          .withUser("root")
          .withTty(true)
          .exec();
      String sExecCmdId = execCmdId.getId();
      Adapter<Frame> runExecCmd = client.execStartCmd(sExecCmdId).exec(logCallback);
      runExecCmd.awaitCompletion();
      InspectExecResponse result = client.inspectExecCmd(sExecCmdId).exec();
      System.out.println(result.getExitCodeLong());

      if (result.getExitCodeLong() != 0) {
        client.removeServiceCmd(serviceResponse.getId()).exec();
      }
      // List<Container> containers = client.listContainersCmd().exec();
      // Optional<Container> taskContainer = containers.stream().filter((container) ->
      // {
      // List<String> names = Arrays.asList(container.getNames());
      // System.out.println(names.toString());
      // return Arrays.asList(container.getNames()).contains("/task01");
      // }).findFirst();

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
    return null;
  }

}
