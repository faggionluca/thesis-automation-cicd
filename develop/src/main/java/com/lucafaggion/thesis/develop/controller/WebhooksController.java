package com.lucafaggion.thesis.develop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.lucafaggion.thesis.develop.repository.RepoEventRepository;
import com.lucafaggion.thesis.develop.repository.RunnerTaskConfigRepository;
import com.lucafaggion.thesis.develop.service.ContextService;
import com.lucafaggion.thesis.develop.service.WebhookService;
import com.lucafaggion.thesis.develop.service.GitHub.GitHubAPIService;
import com.lucafaggion.thesis.develop.util.ExceptionStatusUtils;

@RestController
public class WebhooksController {

  private final static Logger logger = LoggerFactory.getLogger(WebhooksController.class);

  @Autowired
  RepoEventRepository repoEventRepository;

  @Autowired
  RunnerTaskConfigRepository runnerTaskConfigRepository;

  @Autowired
  WebhookService webhookService;

  @Autowired
  GitHubAPIService gitHubAPIService;

  @Autowired
  ContextService contextService;

  @PostMapping("/webhook/event/push")
  ResponseEntity<HttpStatus> ReceiveGeneralPushEvent(@RequestHeader HttpHeaders headers, @RequestBody String body)
      throws JsonMappingException, JsonProcessingException {
    // Deserializziamo l'evento
    logger.debug("Received /webhook/event/push with HEADERS: {}, BODY: {}", headers, body);
    RepoPushEvent repoPushEvent = repoEventRepository.save(webhookService.deserializeToPushEvent(headers, body));
    contextService.getContext().setVariable("event", repoPushEvent);
    logger.debug("Successfully deserialized /webhook/event/push RESULT: {}", repoPushEvent);

    // Recuperiamo la config per l'evento
    try {
      RunnerTaskConfig config = gitHubAPIService.retriveConfig(repoPushEvent);
      runnerTaskConfigRepository.save(config);
      logger.debug("Successfully retrived RunnerTaskConfig with value: {}", config);
      logger.debug("RunnerContext holds : {}", contextService.getContext().getVariableNames());
    } catch (Exception e) {
      logger.error("Exception while retriving config, Type: {}", e.getClass());
      repoPushEvent.setStatus(ExceptionStatusUtils.fromThrowable(e));
      repoEventRepository.save(repoPushEvent);
    }
    return ResponseEntity.ok(HttpStatus.OK);
  }

}
