package com.lucafaggion.thesis.develop.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.repository.RepoEventRepository;

@Service
public class WebhookService {
  
  private final static Logger logger = LoggerFactory.getLogger(WebhookService.class);

  @Autowired
  List<RepoEventWebhookService<RepoPushEvent>> repoEventWebhookServices;

  public RepoPushEvent deserializeToPushEvent(HttpHeaders headers, String body)
      throws JsonMappingException, JsonProcessingException {
    logger.debug("Searching for PushEventWebhookService in LIST:{}", repoEventWebhookServices);
    RepoEventWebhookService<RepoPushEvent> service = repoEventWebhookServices.stream()
        .filter(currservice -> currservice.acceptEvent(headers)).findFirst().orElseThrow();
    return service.deserialize(body);
  }

}
