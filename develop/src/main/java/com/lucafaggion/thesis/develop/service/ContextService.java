package com.lucafaggion.thesis.develop.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Mount;
import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerContext;

import lombok.Getter;

/*
 * Servizio per collezionare dati del context di Thymeleaf
 * Questo servizio e ricreato per ogni richiesta
 */
@Service
@RequestScope
@Getter
public class ContextService {

  public static final String CONTAINER_MOUNTS = "mounts_internal";
  public static final String GLOBAL_MOUNTS = "mounts_global";

  public static final String JOB = "job";
  public static final String TASK = "task";
  public static final String EVENT = "event";
  public static final String REPO = "repository";
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

  public static void mergeContextOn(RunnerContext contextBase, List<RunnerContext> contextsToMerge) {
    // TODO: Eseguire un merge significativo soprattuto sui volumi dichiarati dagli
    // altri context
    contextBase.setVariable(DEBUG_CONTEXTS, contextsToMerge);
    contextsToMerge.forEach((context) -> {
      ContextService.addMountsToContext(contextBase, context.getVariableAs(GLOBAL_MOUNTS));
    });
  }

  public static void addMountsToContext(RunnerContext contextBase, List<Mount> mounts) {

    if (!contextBase.containsVariable(CONTAINER_MOUNTS)) {
      contextBase.setVariable(CONTAINER_MOUNTS, mounts);
      return;
    }

    List<Mount> currentMounts = contextBase.getVariableAs(CONTAINER_MOUNTS);
    List<Mount> combined = Stream.concat(currentMounts.stream(), mounts.stream())
        .distinct()
        .collect(Collectors.toList());
    contextBase.setVariable(CONTAINER_MOUNTS, combined);

  }

  public static void updateGlobalMounts(RunnerContext contextBase, List<InspectContainerResponse.Mount> mounts) {

    Set<String> mountNames = mounts.stream().map((mount) -> mount.getName()).collect(Collectors.toSet());

    if (!contextBase.containsVariable(GLOBAL_MOUNTS)) {
      contextBase.setVariable(GLOBAL_MOUNTS, mountNames);
      return;
    }

    Set<String> currentMounts = contextBase.getVariableAs(GLOBAL_MOUNTS);
    Set<String> combined = Stream.concat(currentMounts.stream(), mountNames.stream())
        .distinct()
        .collect(Collectors.toSet());
    contextBase.setVariable(GLOBAL_MOUNTS, combined);
  }

  public static void setVariablesOfContextFor(RunnerAction action) {
    action.getContext().setVariable(JOB, action.getJob());
    action.getContext().setVariable(TASK, action.getJob().getTaskConfig());
    action.getContext().setVariable(EVENT, action.getJob().getTaskConfig().getEvent());
    action.getContext().setVariable(REPO, action.getJob().getTaskConfig().getEvent().getRepository());
    action.getContext().setVariable(REPO_URL, action.getJob().getTaskConfig().getEvent().getRepository().getUrl());
    action.getContext().setVariable(REPO_HOST, "*");
  }

}
