package com.lucafaggion.thesis.develop.service.GitHub;

import java.io.IOException;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucafaggion.thesis.common.config.AMQPCommonConfig;
import com.lucafaggion.thesis.common.message.SearchUserAssociatedByUserAndService;
import com.lucafaggion.thesis.common.message.SearchUserAssociatedByUsernameAndService;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.develop.model.Repo;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.lucafaggion.thesis.develop.model.GitHub.GitHubFileContentResponse;
import com.lucafaggion.thesis.develop.repository.RepoEventRepository;
import com.lucafaggion.thesis.develop.repository.RepoRepository;
import com.lucafaggion.thesis.develop.service.APIInterceptor;
import com.lucafaggion.thesis.develop.service.ContextService;
import com.lucafaggion.thesis.develop.service.RunnerTaskConfigService;
import com.lucafaggion.thesis.develop.service.exceptions.ConfigurationNotFoundException;
import com.lucafaggion.thesis.develop.util.ExceptionStatusUtils;

@Service
public class GitHubAPIService {

  static String serviceName = "github";

  private final static Logger logger = LoggerFactory.getLogger(GitHubAPIService.class);

  protected final String baseUrl = "https://api.github.com";
  protected final String repoContent = "/repos/{repo_user}/{repo_name}/contents";

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private RabbitTemplate template;

  @Autowired
  private ContextService contextService;

  @Autowired
  private RunnerTaskConfigService runnerTaskConfigService;

  public boolean accept(HttpHeaders headers) {
    return headers.containsKey("x-github-event");
  }

  public boolean accept(String name) {
    return serviceName.equals(name);
  }

  public RunnerTaskConfig retriveConfig(RepoPushEvent repoPushEvent)
      throws HttpClientErrorException, IOException {

    // Aggiungiamo gli Headers se il repo e' privato aggiungiamo
    // AUTHORIZE_USER_HEADER per l'APIInterceptor
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
    String url = baseUrl + repoContent + "/config.yaml?ref={ref}";

    String[] repoUserAndName = repoPushEvent.getRepository().getFull_name().split("/");
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("ref", repoPushEvent.getAfter());
    uriVariables.put("base_url", baseUrl);
    uriVariables.put("repo_user", repoUserAndName[0]);
    uriVariables.put("repo_name", repoUserAndName[1]);

    // Aggiungiamo dati al context
    contextService.getContext().setVariable("repo_user", repoUserAndName[0]);
    contextService.getContext().setVariable("repo_name", repoUserAndName[1]);

    // Aggiungiamo il token al context
    SearchUserAssociatedByUsernameAndService search = SearchUserAssociatedByUsernameAndService.builder().username(repoUserAndName[0])
    .serviceName(serviceName).build();
    Optional<UserAssociatedAccount> userAssociatedAccount = Optional
    .ofNullable((UserAssociatedAccount) template.convertSendAndReceive(AMQPCommonConfig.USER_EXCHANGE,
        AMQPCommonConfig.USER_ROUTE_KEY,
        search));

    if (userAssociatedAccount.isPresent()) {
      contextService.getContext().setVariable("service_token", userAssociatedAccount.get().getToken());
    }

    // eseguiamo la richesta
    try {
      ResponseEntity<GitHubFileContentResponse> response = this.restTemplate.exchange(url, HttpMethod.GET, request,
          GitHubFileContentResponse.class,
          uriVariables);
      if (response.getStatusCode() == HttpStatus.OK) {
        return runnerTaskConfigService.from(Base64.decodeBase64(response.getBody().getContent()));
      }
    } catch (HttpClientErrorException.NotFound e) {
      throw new ConfigurationNotFoundException(e);
    }
    return null;
  }
}
