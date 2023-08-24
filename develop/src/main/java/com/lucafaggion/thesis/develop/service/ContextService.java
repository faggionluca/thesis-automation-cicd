package com.lucafaggion.thesis.develop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

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
  
  private final RunnerContext context;

  public ContextService() {
    this.context = new RunnerContext();
  }

  public static void mergeContextOn(RunnerContext contextBase, List<RunnerContext> contextsToMerge) {
    // TODO: Eseguire un merge significativo soprattuto sui volumi dichiarati dagli altri context
    contextBase.setVariable("previous", contextsToMerge);
  }

}
