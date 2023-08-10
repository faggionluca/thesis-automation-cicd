package com.lucafaggion.thesis.develop.service;

import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.GitHub.GitHubPushEvent;

public interface RepoEventWebhookService<T> {

  boolean acceptEvent(HttpHeaders headers);

  /**
   * Converte un body di typo GitHubPushEvent in un oggetto RepoPushEvent
   * gestibile da noi
   * 
   * @param body il body della richiesta da convertire
   * @return RepoPushEvent
   */
  T deserialize(String body) throws JsonMappingException, JsonProcessingException;

}