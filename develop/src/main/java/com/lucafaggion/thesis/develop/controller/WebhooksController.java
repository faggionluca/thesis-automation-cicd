package com.lucafaggion.thesis.develop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lucafaggion.thesis.develop.model.RepoPushEvent;

@RestController
public class WebhooksController {
  
  @PostMapping("/webhook/event/push")
  ResponseEntity<HttpStatus> ReceivePushEvent(@RequestBody RepoPushEvent repoPushEvent) {
    return ResponseEntity.ok(HttpStatus.OK);
  }
}
