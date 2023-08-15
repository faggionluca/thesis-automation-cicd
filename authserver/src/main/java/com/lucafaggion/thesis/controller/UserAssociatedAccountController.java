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
import com.lucafaggion.thesis.model.oauth.OAuthTokenRequest;
import com.lucafaggion.thesis.model.oauth.OAuthTokenResponse;
import com.lucafaggion.thesis.repository.ExternalServiceRepository;
import com.lucafaggion.thesis.repository.UserRepository;
import com.lucafaggion.thesis.service.BitBucketAssociatedAccountService;
import com.lucafaggion.thesis.service.GitHubAssociatedAccountService;

@RestController
public class UserAssociatedAccountController {

  @Autowired
  GitHubAssociatedAccountService gitHubAssociatedAccountService;

  @Autowired
  BitBucketAssociatedAccountService bitBucketAssociatedAccountService;

  private final static Logger logger = LoggerFactory.getLogger(UserAssociatedAccountController.class);

  // ----------------------- GITHUB -----------------------------

  @GetMapping("/user/add/github")
  public ModelAndView addGitHubAccount() {
    return gitHubAssociatedAccountService.redirectToAuthorize();
  }

  @GetMapping("/user/gh/callback")
  @ResponseBody
  public void callbackGitHub(@RequestParam String code, Authentication authentication) {
    logger.debug("Code callback is {}", code);
    gitHubAssociatedAccountService.exchangeAndSave(authentication, code);
  }

  // ----------------------- BITBUCKET -----------------------------

  @GetMapping("/user/add/bitbucket")
  public ModelAndView addBitBucketAccount() {
    return bitBucketAssociatedAccountService.redirectToAuthorize();
  }

  @GetMapping("/user/bucket/callback")
  @ResponseBody
  public void callbackBitBucket(@RequestParam String code, Authentication authentication) {
    logger.debug("Code callback is {}", code);
    bitBucketAssociatedAccountService.exchangeAndSave(authentication, code);
  }
}
