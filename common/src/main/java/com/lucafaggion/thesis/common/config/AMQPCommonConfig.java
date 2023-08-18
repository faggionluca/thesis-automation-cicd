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

  // public static final String SEARCH_USER_FROM_ASSOCIATED_ROUTE_KEY =
  // "search-user-associated";
  // public static final String SEARCH_USER_ASSOCIATED_FROM_USER_ID_ROUTE_KEY =
  // "search-user-associated-from-user-id";
  public static final String USER_EXCHANGE = "x.user";
  // public static final String USER_EXCHANGE_BEAN = "userExchange";

  public static final String USER_ROUTE_KEY = "user_rk";

  // @Bean
  // public DirectExchange userExchange() {
  // return new DirectExchange(USER_EXCHANGE);
  // }

  @Bean
  public MessageConverter jackson2MessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

}
