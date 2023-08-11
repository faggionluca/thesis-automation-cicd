package com.lucafaggion.thesis.develop.service.GitHub;

import java.text.MessageFormat;
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
import org.springframework.web.client.RestTemplate;

import com.lucafaggion.thesis.common.config.AMQPCommonConfig;
import com.lucafaggion.thesis.common.message.SearchUserAssociatedByUserAndService;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.develop.model.Repo;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;

@Service
public class GitHubAPIService {

  static String serviceName = "github";

  private final static Logger logger = LoggerFactory.getLogger(GitHubAPIService.class);
  private final RestTemplate restTemplate;

  @Autowired
  private RabbitTemplate template;

  public GitHubAPIService(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public boolean accept(HttpHeaders headers) {
    return headers.containsKey("x-github-event");
  }

  public RunnerTaskConfig retriveConfig(RepoPushEvent repoPushEvent) {

    Optional<UserAssociatedAccount> userAssociatedAccount = Optional.empty();
    SearchUserAssociatedByUserAndService search = SearchUserAssociatedByUserAndService.builder()
        .id(repoPushEvent.getRepository().getOwner()).serviceName(serviceName).build();

    if (repoPushEvent.getPusher() != null) {
      search.setId(repoPushEvent.getPusher());
      userAssociatedAccount = Optional
          .of((UserAssociatedAccount) template.convertSendAndReceive(AMQPCommonConfig.USER_EXCHANGE,
              AMQPCommonConfig.SEARCH_USER_ASSOCIATED_FROM_USER_ID_ROUTE_KEY,
              search));
    } else {
      userAssociatedAccount = Optional
          .of((UserAssociatedAccount) template.convertSendAndReceive(AMQPCommonConfig.USER_EXCHANGE,
              AMQPCommonConfig.SEARCH_USER_ASSOCIATED_FROM_USER_ID_ROUTE_KEY,
              search));
    }

    String token = userAssociatedAccount.map(account -> account.getToken()).orElseThrow(() -> new IllegalStateException(
        "No userAssociatedAccount found for Repo " + repoPushEvent.getRepository().getUrl()));

    HttpHeaders headers = new HttpHeaders();
    if(repoPushEvent.getRepository().getIsPrivate()){
      headers.set("Authorization", "Bearer " + token);
    }
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    // build the request
    HttpEntity<String> request = new HttpEntity<String>(headers);

    String url = "https://api.github.com/repos/{repo_user}/{repo_name}/contents/test.txt?ref={ref}";

    String[] repoUserAndName = repoPushEvent.getRepository().getFull_name().split("/");
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("ref", repoPushEvent.getAfter());
    uriVariables.put("repo_user", repoUserAndName[0]);
    uriVariables.put("repo_name", repoUserAndName[1]);
    // use `exchange` method for HTTP call
    ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, request, String.class,
        uriVariables);
    if (response.getStatusCode() == HttpStatus.OK) {
      logger.debug(response.getBody());
      return RunnerTaskConfig.builder().build();
    }

    return null;
  }

}
