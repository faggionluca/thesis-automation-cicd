package com.lucafaggion.thesis.controller;

import java.math.BigInteger;

import org.hibernate.Hibernate;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lucafaggion.thesis.common.config.AMQPCommonConfig;
import com.lucafaggion.thesis.common.message.SearchUserAssociatedByUserAndService;
import com.lucafaggion.thesis.common.message.SearchUserByUsernameAndService;
import com.lucafaggion.thesis.common.model.ExternalService;
import com.lucafaggion.thesis.common.model.User;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.config.AMQPServerConfig;
import com.lucafaggion.thesis.repository.ExternalServiceRepository;
import com.lucafaggion.thesis.repository.UserAssociatedAccountRepository;
import com.lucafaggion.thesis.repository.UserRepository;

@Component
@Transactional
// @RabbitListener(queues = AMQPServerConfig.USER_SEARCH_QUEUE)
@RabbitListener(bindings = @QueueBinding(exchange = @Exchange(name = AMQPCommonConfig.USER_EXCHANGE), value = @Queue(value = AMQPServerConfig.USER_SEARCH_QUEUE), key = AMQPCommonConfig.USER_ROUTE_KEY))
public class UserMessageConsumer {

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserAssociatedAccountRepository userAssociatedAccountRepository;

  @RabbitHandler
  public User searchUserByUsernameOnServiceUser(SearchUserByUsernameAndService request) {
    return userRepository.findByUsernameOnService(request.getUsername(), request.getServiceName()).orElse(null);
  }

  @RabbitHandler
  public UserAssociatedAccount searchUserAssociatedAccountByUserIdAndServiceName(
      SearchUserAssociatedByUserAndService request) {
    UserAssociatedAccount user = userAssociatedAccountRepository
        .findByUserIdAndServiceName(request.getId(), request.getServiceName()).orElse(null);
    return user;
  }
}
