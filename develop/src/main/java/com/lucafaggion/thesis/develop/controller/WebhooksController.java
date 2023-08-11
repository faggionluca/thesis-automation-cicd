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
import com.lucafaggion.thesis.develop.repository.RepoEventRepository;
import com.lucafaggion.thesis.develop.service.WebhookService;
import com.lucafaggion.thesis.develop.service.GitHub.GitHubAPIService;

@RestController
public class WebhooksController {
  
  private final static Logger logger = LoggerFactory.getLogger(WebhooksController.class);

  @Autowired
  RepoEventRepository repoEventRepository;

  @Autowired
  WebhookService webhookService;

  @Autowired
  GitHubAPIService gitHubAPIService;

  // @PostMapping("/webhook/gh/event/push")
  // ResponseEntity<HttpStatus> ReceivePushEvent(@RequestBody GitHubPushEvent gitHubPushEvent) {
  //   RepoPushEvent repoPushEvent = gitHubWebhookService.toRepoPushEvent(gitHubPushEvent);
  //   repoEventRepository.save(repoPushEvent);
  //   return ResponseEntity.ok(HttpStatus.OK);
  // }

  @PostMapping("/webhook/event/push")
  ResponseEntity<HttpStatus> ReceiveGeneralPushEvent(@RequestHeader HttpHeaders headers, @RequestBody String body)
      throws JsonMappingException, JsonProcessingException {
    logger.debug("Received /webhook/event/push with HEADERS: {}, BODY: {}", headers, body);
    RepoPushEvent repoPushEvent = repoEventRepository.save(webhookService.deserializeToPushEvent(headers, body));
    logger.debug("Successfully deserialized /webhook/event/push RESULT: {}", repoPushEvent);
    gitHubAPIService.retriveConfig(repoPushEvent);
    return ResponseEntity.ok(HttpStatus.OK);
  }

}
