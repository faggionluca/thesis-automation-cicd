package com.lucafaggion.thesis.develop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.GitHub.GitHubPushEvent;
import com.lucafaggion.thesis.develop.repository.GitHubWebhookService;
import com.lucafaggion.thesis.develop.repository.RepoEventRepository;

@RestController
public class WebhooksController {
  
  @Autowired
  GitHubWebhookService gitHubWebhookService;

  @Autowired
  RepoEventRepository repoEventRepository;

  @PostMapping("/webhook/gh/event/push")
  ResponseEntity<HttpStatus> ReceivePushEvent(@RequestBody GitHubPushEvent gitHubPushEvent) {
    RepoPushEvent repoPushEvent = gitHubWebhookService.toRepoPushEvent(gitHubPushEvent);
    repoEventRepository.save(repoPushEvent);
    return ResponseEntity.ok(HttpStatus.OK);
  }
}
