package com.lucafaggion.thesis.controller;

import java.math.BigInteger;

import org.hibernate.Hibernate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lucafaggion.thesis.common.message.SearchUserAssociatedByUserAndService;
import com.lucafaggion.thesis.common.message.SearchUserMessage;
import com.lucafaggion.thesis.common.model.ExternalService;
import com.lucafaggion.thesis.common.model.User;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.repository.ExternalServiceRepository;
import com.lucafaggion.thesis.repository.UserAssociatedAccountRepository;
import com.lucafaggion.thesis.repository.UserRepository;
import com.lucafaggion.thesis.config.AMQPServerConfig;

@Component
@Transactional
public class MessageBroker {

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserAssociatedAccountRepository userAssociatedAccountRepository;

  @RabbitListener(queues = AMQPServerConfig.USER_SEARCH_QUEUE)
  public User searchUser(SearchUserMessage searchUserMessage) {
    return userRepository.findByUsernameOnService(searchUserMessage.getUsername(), searchUserMessage.getServiceName())
        .orElse(null);
  }

  @RabbitListener(queues = AMQPServerConfig.USER_ASSOCIATED_FROM_USER_ID_SEARCH_QUEUE)
  public UserAssociatedAccount searchUser(SearchUserAssociatedByUserAndService searchUserAssociatedByUserAndService) {
    UserAssociatedAccount user = userAssociatedAccountRepository
        .findByUserIdAndServiceName(searchUserAssociatedByUserAndService.getId(),
            searchUserAssociatedByUserAndService.getServiceName())
        .orElse(null);
    return user;
  }
}