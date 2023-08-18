package com.lucafaggion.thesis.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lucafaggion.thesis.common.config.AMQPCommonConfig;
import com.lucafaggion.thesis.common.message.RefreshTokenForUserAssociatedOnService;
import com.lucafaggion.thesis.common.message.SearchUserAssociatedByUserAndService;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;
import com.lucafaggion.thesis.config.AMQPServerConfig;
import com.lucafaggion.thesis.repository.UserAssociatedAccountRepository;
import com.lucafaggion.thesis.service.interfaces.UserAssociatedAccountService;

/**
 * ExternalServiceMessageConsumer
 */
@Component
@Transactional
// @RabbitListener(queues = AMQPServerConfig.USER_SEARCH_QUEUE)
@RabbitListener(bindings = @QueueBinding(exchange = @Exchange(name = AMQPCommonConfig.EXTERNAL_SERVICE_EXCHANGE), value = @Queue(value = AMQPServerConfig.EXTERNAL_SERVICE_QUEUE), key = AMQPCommonConfig.EXTERNAL_SERVICE_ROUTE_KEY))
public class ExternalServiceMessageConsumer {

  @Autowired
  List<UserAssociatedAccountService> userAssociatedAccountServices;

  @Autowired
  UserAssociatedAccountRepository userAssociatedAccountRepository;

  @RabbitHandler
  public UserAssociatedAccount refreshTokenForUserAssociatedOnService(RefreshTokenForUserAssociatedOnService request) {
    UserAssociatedAccountService service = userAssociatedAccountServices.stream()
    .filter(currservice -> currservice.forService(request.getServiceName())).findFirst().orElseThrow();
    return service.refreshTokenFor(request.getUserAssociatedAccount());
  }
  
  @RabbitHandler
  public UserAssociatedAccount refreshTokenForUserAssociatedOnService(SearchUserAssociatedByUserAndService request) {
    Optional<UserAssociatedAccount> user = userAssociatedAccountRepository
    .findByUserIdAndServiceName(request.getId(), request.getServiceName());
    if (user.isPresent()) {
      UserAssociatedAccountService service = userAssociatedAccountServices.stream()
      .filter(currservice -> currservice.forService(request.getServiceName())).findFirst().orElseThrow();
      return service.refreshTokenFor(user.get());
    }
    return null;
  }
}