package com.lucafaggion.thesis.develop.service;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucafaggion.thesis.common.config.AMQPCommonConfig;
import com.lucafaggion.thesis.common.message.SearchUserMessage;
import com.lucafaggion.thesis.common.model.ExternalService;
import com.lucafaggion.thesis.common.model.User;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.develop.model.Repo;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.GitHub.GitHubPushEvent;
import com.lucafaggion.thesis.develop.repository.RepoRepository;

@Service
public class GitHubWebhookService implements RepoEventWebhookService<RepoPushEvent> {

  static String serviceName = "github";

  @Autowired
  RepoRepository repoRepository;

  @Autowired
  private RabbitTemplate template;

  @Autowired
  private ObjectMapper mapper;

  @Override
  public boolean acceptEvent(HttpHeaders headers){
    return headers.containsKey("x-github-event");
  }

  /**
   * Converte un body di typo GitHubPushEvent in un oggetto RepoPushEvent
   * gestibile da noi
   * 
   * @param gitHubPushEvent il body della richiesta da convertire
   * @return RepoPushEvent
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Override
  public RepoPushEvent deserialize(String body) throws JsonMappingException, JsonProcessingException {

    GitHubPushEvent gitHubPushEvent = mapper.readValue(body, GitHubPushEvent.class);

    SearchUserMessage searchUserMessage = SearchUserMessage.builder()
        .username(gitHubPushEvent.getPusher().getName())
        .serviceName(serviceName)
        .build();

    User pusher = (User)template.convertSendAndReceive(AMQPCommonConfig.USER_EXCHANGE,
        AMQPCommonConfig.SEARCH_USER_FROM_ASSOCIATED_ROUTE_KEY,
        searchUserMessage);

    if(pusher == null){
      return null;
    }

    Repo repo = repoRepository.findByUrl(gitHubPushEvent.getRepository().getClone_url()).orElseThrow();

    RepoPushEvent repoPushEvent = RepoPushEvent.builder()
        .ref(gitHubPushEvent.getRef())
        .repository(repo)
        .after(gitHubPushEvent.getAfter())
        .before(gitHubPushEvent.getBefore())
        .created(gitHubPushEvent.isCreated())
        .deleted(gitHubPushEvent.isDeleted())
        .forced(gitHubPushEvent.isForced())
        .pusher(BigInteger.valueOf(pusher.getId()))
        .build();

    return repoPushEvent;
  }

}
