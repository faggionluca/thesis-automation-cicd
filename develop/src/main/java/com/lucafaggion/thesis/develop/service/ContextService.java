package com.lucafaggion.thesis.develop.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

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

}
