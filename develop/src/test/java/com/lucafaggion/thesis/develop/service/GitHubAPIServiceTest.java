package com.lucafaggion.thesis.develop.service;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.lucafaggion.thesis.common.config.AMQPCommonConfig;
import com.lucafaggion.thesis.common.message.SearchUserAssociatedByUserAndService;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.develop.service.GitHub.GitHubAPIService;

public class GitHubAPIServiceTest extends ServiceFixtures {

  @Autowired
  GitHubAPIService apiService;

  @Autowired
  RabbitTemplate rabbitTemplate;

  @Test
  void testRetriveConfig() {
    System.out.println("test");
    SearchUserAssociatedByUserAndService message = SearchUserAssociatedByUserAndService.builder().id(BigInteger.ONE).serviceName("github").build();
    UserAssociatedAccount user = (UserAssociatedAccount) rabbitTemplate.convertSendAndReceive(
        AMQPCommonConfig.USER_EXCHANGE,
        AMQPCommonConfig.SEARCH_USER_ASSOCIATED_FROM_USER_ID_ROUTE_KEY, message);
    System.out.println(user);
  }

}
