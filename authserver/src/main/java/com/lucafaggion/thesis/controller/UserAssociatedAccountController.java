package com.lucafaggion.thesis.controller;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.lucafaggion.thesis.common.model.ExternalService;
import com.lucafaggion.thesis.common.model.User;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.model.CustomUserDetails;
import com.lucafaggion.thesis.model.github.GitHubTokenExchange;
import com.lucafaggion.thesis.model.github.GitHubTokenResponse;
import com.lucafaggion.thesis.repository.ExternalServiceRepository;
import com.lucafaggion.thesis.repository.UserRepository;
import com.lucafaggion.thesis.service.GitHubAssociatedAccountService;

@RestController
public class UserAssociatedAccountController {

  @Autowired
  GitHubAssociatedAccountService gitHubAssociatedAccountService;
  // @Autowired
  // UserRepository userRepository;
  // @Autowired
  // ExternalServiceRepository externalServiceRepository;

  // @Value("${com.lucafaggion.oauth.client.github.client-id}")
  // private String githubClientId;
  // @Value("${com.lucafaggion.oauth.client.github.client-secret}")
  // private String githubClientSecret;
  // @Value("${com.lucafaggion.oauth.client.github.uri}")
  // private String githubUri;
  // @Value("${com.lucafaggion.oauth.client.github.scopes}")
  // private String githubScopes;
  // @Value("${com.lucafaggion.oauth.client.github.access-token-uri}")
  // private String githubAccessTokenUri;
  // @Value("${com.lucafaggion.oauth.client.github.service-name}")
  // private String githubServiceName;

  private final static Logger logger = LoggerFactory.getLogger(UserAssociatedAccountController.class);

  @GetMapping("/user/add/github")
  public ModelAndView addGitHubAccount() {
    return gitHubAssociatedAccountService.redirectToAuthorize();
    // String uri = String.format(githubUri + "?client_id=%s&scope=%s&state=randomstring", githubClientId, githubScopes);
    // return new ModelAndView("redirect:" + uri);
  }

  @GetMapping("/user/gh/callback")
  @ResponseBody
  public void callbackGitHub(@RequestParam String code, Authentication authentication) {
    // TODO: astrarre tutto il codice per poter essere riutilizzato da diffenti
    // servizi, bitcucket github etc!!
    // TODO: spostare tutti le classi POJO legate ai servizi nel progetto common!!
    logger.debug("Code callback is {}", code);

    gitHubAssociatedAccountService.exchangeAndSave(authentication, code);
    // RestTemplate restTemplate = new RestTemplate();

    // HttpHeaders headers = new HttpHeaders();
    // headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    // GitHubTokenExchange exGitHubTokenExchange =
    // GitHubTokenExchange.builder().client_id(githubClientId)
    // .client_secret(githubClientSecret).code(code).build();
    // // Creaiamo la richiesta
    // HttpEntity<GitHubTokenExchange> request = new
    // HttpEntity<GitHubTokenExchange>(exGitHubTokenExchange, headers);    // // eseguiamo la richesta
    // ResponseEntity<GitHubTokenResponse> response =
    // restTemplate.exchange(githubAccessTokenUri, HttpMethod.POST, request,
    // GitHubTokenResponse.class);
    // if (response.getStatusCode() == HttpStatus.OK) {
    // GitHubTokenResponse tokenResponse = response.getBody();

    // // TODO: rimodulare UserAssociatedAccount con sottoclassi
    // GithubAssociatedAccount, BitbucketAssociatedAccount etc.
    // // TODO: recuperare i dettagli dell'account e salvarli insieme al token

    // CustomUserDetails userDetails = (CustomUserDetails)
    // authentication.getPrincipal();
    // User user =
    // userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    // ExternalService service =
    // externalServiceRepository.findByName(githubServiceName).orElseThrow();
    // UserAssociatedAccount userAssociatedAccount = UserAssociatedAccount.builder()
    // .token(tokenResponse.getAccess_token())
    // .service(service)
    // .build();

    // user.getUserAssociatedAccounts().add(userAssociatedAccount);
    // userRepository.save(user);
    // logger.debug(tokenResponse.toString());
    // }
  }
}
