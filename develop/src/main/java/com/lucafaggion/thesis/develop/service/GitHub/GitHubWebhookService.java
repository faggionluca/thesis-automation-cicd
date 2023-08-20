package com.lucafaggion.thesis.develop.service.GitHub;

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
import com.lucafaggion.thesis.common.message.SearchUserByUsernameAndService;
import com.lucafaggion.thesis.common.model.ExternalService;
import com.lucafaggion.thesis.common.model.User;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.develop.model.Repo;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.GitHub.GitHubPushEvent;
import com.lucafaggion.thesis.develop.repository.RepoRepository;
import com.lucafaggion.thesis.develop.service.RepoEventWebhookService;

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
  public boolean acceptEvent(HttpHeaders headers) {
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

    SearchUserByUsernameAndService searchUserMessage = SearchUserByUsernameAndService.builder()
        .username(gitHubPushEvent.getPusher().getName())
        .serviceName(serviceName)
        .build();

    Optional<User> pusher = Optional.ofNullable((User) template.convertSendAndReceive(AMQPCommonConfig.USER_EXCHANGE,
        AMQPCommonConfig.USER_ROUTE_KEY,
        searchUserMessage));

    Repo repo = repoRepository.findByUrl(gitHubPushEvent.getRepository().getClone_url()).orElseThrow();

    RepoPushEvent repoPushEvent = RepoPushEvent.builder()
        .ref(gitHubPushEvent.getRef())
        .repository(repo)
        .after(gitHubPushEvent.getAfter())
        .before(gitHubPushEvent.getBefore())
        .created(gitHubPushEvent.isCreated())
        .deleted(gitHubPushEvent.isDeleted())
        .forced(gitHubPushEvent.isForced())
        .build();

    if (pusher.isPresent()) {
      repoPushEvent.setPusher(BigInteger.valueOf(pusher.get().getId()));
    }

    return repoPushEvent;
  }

}
