package com.lucafaggion.thesis.develop.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import com.github.dockerjava.api.model.Mount;
import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerContext;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;

import lombok.Getter;

/*
 * Servizio per collezionare dati del context di Thymeleaf
 * Questo servizio e ricreato per ogni richiesta
 */
@Service
@RequestScope
@Getter
public class ContextService {

  public static final String TAG_LABEL = "com.lucafaggion.thesis";
  public static final String VOLUME_LABEL = "com.lucafaggion.thesis.task";

  public static final String CONTAINER_MOUNTS = "mounts_internal";
  public static final String DECLARED_OUT_MOUNTS = "mounts_output";

  public static final String JOB = "job";
  public static final String TASK = "task";
  public static final String EVENT = "event";
  public static final String REPO = "repository";
  public static final String REPO_PATH = "repository_path";
  public static final String REPO_USER = "user";
  public static final String REPO_NAME = "name";
  public static final String REPO_TOKEN = "token";
  public static final String REPO_HOST = "host";
  public static final String REPO_URL = "repository_url";
  public static final String DEBUG_CONTEXTS = "DEBUG_PREVIOUS_CONTEXTS";

  private final RunnerContext context;

  public ContextService() {
    this.context = new RunnerContext();
  }

  public static <T> Predicate<T> distinctByKey(
      Function<? super T, ?> keyExtractor) {

    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }

  public static void mergeContextOn(RunnerContext contextBase, List<RunnerContext> contextsToMerge) {
    // TODO: Eseguire un merge significativo soprattuto sui volumi dichiarati dagli
    // altri context
    contextBase.setVariable(DEBUG_CONTEXTS, contextsToMerge);

    Map<String, Mount> mergedDeclaredMounts = contextsToMerge.stream()
        .map(context -> Optional.ofNullable((Map<String, Mount>) context.getVariableAs(DECLARED_OUT_MOUNTS)))
        .filter(value -> value.isPresent())
        .flatMap(m -> m.get().entrySet().stream()).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    contextBase.setVariable(DECLARED_OUT_MOUNTS, mergedDeclaredMounts);

    ContextService.addMountsToContext(contextBase,
        mergedDeclaredMounts.entrySet().stream().map(Entry::getValue).collect(Collectors.toList()));

    mergedDeclaredMounts.forEach((name, mount) -> contextBase.setVariable(name, mount.getTarget()));
  }

  public static void addOutputToContext(RunnerContext contextBase, Map<String, Mount> output) {

    if (!contextBase.containsVariable(DECLARED_OUT_MOUNTS)) {
      contextBase.setVariable(DECLARED_OUT_MOUNTS, output);
      return;
    }

    Map<String, Mount> currentDeclared = contextBase.getVariableAs(DECLARED_OUT_MOUNTS);
    Map<String, Mount> combined = Stream.concat(currentDeclared.entrySet().stream(), output.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));

    contextBase.setVariable(DECLARED_OUT_MOUNTS, combined);

  }

  public static void addMountsToContext(RunnerContext contextBase, List<Mount> mounts) {

    if (!contextBase.containsVariable(CONTAINER_MOUNTS)) {
      contextBase.setVariable(CONTAINER_MOUNTS, mounts);
      return;
    }

    List<Mount> currentMounts = contextBase.getVariableAs(CONTAINER_MOUNTS);
    List<Mount> combined = Stream.concat(currentMounts.stream(), mounts.stream())
        .filter(distinctByKey(mount -> mount.getTarget()))
        .collect(Collectors.toList());
    contextBase.setVariable(CONTAINER_MOUNTS, combined);

  }

  public static void setVariablesOfContextFor(RunnerAction action) {
    action.getContext().setVariable(JOB, action.getJob());
    action.getContext().setVariable(TASK, action.getJob().getTaskConfig());
    action.getContext().setVariable(EVENT, action.getJob().getTaskConfig().getEvent());
    action.getContext().setVariable(REPO, action.getJob().getTaskConfig().getEvent().getRepository());
    action.getContext().setVariable(REPO_URL, action.getJob().getTaskConfig().getEvent().getRepository().getUrl());
    action.getContext().setVariable(REPO_HOST, "*");
  }

  public static List<String> getVolumesToCleanFor(RunnerAction action) {
    // TODO: implmentare il filtraggio
    List<Mount> mounts = action.getContext().getVariableAs(CONTAINER_MOUNTS);
    return mounts.stream().map(mount -> mount.getSource()).collect(Collectors.toList());
  }

  public static Map<String, String> volumeLabelsFor(RunnerContext context){
    RunnerTaskConfig task = context.getVariableAs(TASK);
    return Map.of(TAG_LABEL, "true", VOLUME_LABEL, String.valueOf(task.getId().intValue()));
  }
}
