package com.lucafaggion.thesis.develop.service.GitHub;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucafaggion.thesis.common.config.AMQPCommonConfig;
import com.lucafaggion.thesis.common.message.SearchUserAssociatedByUserAndService;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.develop.model.Repo;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.lucafaggion.thesis.develop.service.APIInterceptor;

@Service
public class GitHubAPIService {

  static String serviceName = "github";

  private final static Logger logger = LoggerFactory.getLogger(GitHubAPIService.class);

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ObjectMapper mapper;

  public boolean accept(HttpHeaders headers) {
    return headers.containsKey("x-github-event");
  }

  public boolean accept(String name) {
    return serviceName.equals(name);
  }

  public RunnerTaskConfig retriveConfig(RepoPushEvent repoPushEvent) throws JsonMappingException, JsonProcessingException, HttpClientErrorException {

    // Aggiungiamo gli Headers se il repo e' privato aggiungiamo AUTHORIZE_USER_HEADER per l'APIInterceptor
    // APIInterceptor aggiungera automaticamente il corretto Bearer token
    HttpHeaders headers = new HttpHeaders();
    if (repoPushEvent.getRepository().getIsPrivate()) {
      BigInteger user = repoPushEvent.getPusher();
      if (user == null) {
        user = repoPushEvent.getRepository().getOwner();
      }
      headers.put(APIInterceptor.AUTHORIZE_USER_HEADER, Arrays.asList(String.valueOf(user), serviceName));
    }

    // Creaiamo la richiesta
    HttpEntity<String> request = new HttpEntity<String>(headers);
    String url = "https://api.github.com/repos/{repo_user}/{repo_name}/contents/config.yml?ref={ref}";

    String[] repoUserAndName = repoPushEvent.getRepository().getFull_name().split("/");
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("ref", repoPushEvent.getAfter());
    uriVariables.put("repo_user", repoUserAndName[0]);
    uriVariables.put("repo_name", repoUserAndName[1]);

    // eseguiamo la richesta
    try {
      ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, request, String.class,
      uriVariables);
      if (response.getStatusCode() == HttpStatus.OK) {
        logger.debug(response.getBody());
        return mapper.readValue(response.getBody(), RunnerTaskConfig.class);
      }
    } catch (Exception e) {
      // TODO: handle exception, settare lo status to ERROR con il messaggio (e.getMessage())
      throw e;
    }
    return null;
  }

}
