package com.lucafaggion.thesis.develop.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.common.repository.UserAssociatedAccountRepository;
import com.lucafaggion.thesis.common.repository.UserRepository;
import com.lucafaggion.thesis.develop.model.Repo;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;
import com.lucafaggion.thesis.develop.model.GitHub.GitHubPushEvent;

@Service
public class GitHubWebhookService {

  static String serviceName = "github";

  @Autowired
  UserAssociatedAccountRepository userAssociatedAccountRepository;

  @Autowired
  RepoRepository repoRepository;

  /**
   * Converte un body di typo GitHubPushEvent in un oggetto RepoPushEvent gestibile da noi
   * @param gitHubPushEvent il body della richiesta da convertire
   * @return RepoPushEvent
   */
  public RepoPushEvent toRepoPushEvent(GitHubPushEvent gitHubPushEvent) {

    UserAssociatedAccount pusher = userAssociatedAccountRepository
        .findByUsernameAndService(gitHubPushEvent.getPusher().getName(), GitHubWebhookService.serviceName)
        .orElse(null);

    Repo repo = repoRepository.findByUrl(gitHubPushEvent.getRepository().getClone_url()).orElseThrow();

    RepoPushEvent repoPushEvent = RepoPushEvent.builder()
        .ref(gitHubPushEvent.getRef())
        .repository(repo)
        .after(gitHubPushEvent.getAfter())
        .before(gitHubPushEvent.getBefore())
        .created(gitHubPushEvent.isCreated())
        .deleted(gitHubPushEvent.isDeleted())
        .forced(gitHubPushEvent.isForced())
        .pusher(pusher.getUser())
        .type(GitHubWebhookService.serviceName)
        .build();

    return repoPushEvent;
  }

}
