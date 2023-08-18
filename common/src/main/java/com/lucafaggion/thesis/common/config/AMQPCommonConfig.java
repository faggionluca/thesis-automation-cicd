package com.lucafaggion.thesis.common.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lucafaggion.thesis.common.message.SearchUserAssociatedByUserAndService;
import com.lucafaggion.thesis.common.message.SearchUserByUsernameAndService;

@Configuration
public class AMQPCommonConfig {

  /*
   * USER_EXCHANGE
   */
  public static final String USER_EXCHANGE = "x.user";
  public static final String USER_ROUTE_KEY = "user_rk";

  /*
  * EXTERNAL_SERVICE_EXCHANGE
  */
  public static final String EXTERNAL_SERVICE_EXCHANGE = "x.external.service";
  public static final String EXTERNAL_SERVICE_ROUTE_KEY = "external_service_rk";

  @Bean
  public MessageConverter jackson2MessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

}
