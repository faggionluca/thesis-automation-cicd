package com.lucafaggion.thesis.develop.service;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

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
public class GitHubWebhookService {

  static String serviceName = "github";

  // @Autowired
  // UserAssociatedAccountRepository userAssociatedAccountRepository;

  @Autowired
  RepoRepository repoRepository;

  // @Autowired
  // ExternalServiceRepository externalServiceRepository;

  @Autowired
  private RabbitTemplate template;

  /**
   * Converte un body di typo GitHubPushEvent in un oggetto RepoPushEvent
   * gestibile da noi
   * 
   * @param gitHubPushEvent il body della richiesta da convertire
   * @return RepoPushEvent
   */
  public RepoPushEvent toRepoPushEvent(GitHubPushEvent gitHubPushEvent) {

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
    // ExternalService service =
    // externalServiceRepository.findByName(serviceName).orElseThrow();

    // // TODO: recuperare il corretto service con una query
    // UserAssociatedAccount pusher = userAssociatedAccountRepository
    // .findByUsernameAndServiceId(gitHubPushEvent.getPusher().getName(),
    // service.getId())
    // .orElse(null);

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
