package com.lucafaggion.thesis.develop.service;

import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface RepoEventWebhookService<T> {

  boolean acceptEvent(HttpHeaders headers);

  /**
   * Converte un body di una HTTP Request in un oggetto di tipo T
   * @param body il body della richiesta da convertire
   * @return T
   */
  T deserialize(String body) throws JsonMappingException, JsonProcessingException;

}