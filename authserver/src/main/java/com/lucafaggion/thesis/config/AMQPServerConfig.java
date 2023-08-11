package com.lucafaggion.thesis.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucafaggion.thesis.common.config.AMQPCommonConfig;

@Configuration
public class AMQPServerConfig {
  
  public static final String USER_SEARCH_QUEUE = "q.user-search.request";
  public static final String USER_ASSOCIATED_FROM_USER_ID_SEARCH_QUEUE = "q.user-associated.from-user-id.request";

  @Bean
  public Queue userSearchQueue() {
    return new Queue(USER_SEARCH_QUEUE);
  }

  @Bean
  public Queue userAssociatedFromUserId() {
    return new Queue(USER_ASSOCIATED_FROM_USER_ID_SEARCH_QUEUE);
  }

  @Bean
  public Binding userSearchBinding(
      @Qualifier(AMQPCommonConfig.USER_EXCHANGE_BEAN) DirectExchange exchange) {
    return BindingBuilder
        .bind(userSearchQueue())
        .to(exchange)
        .with(AMQPCommonConfig.SEARCH_USER_FROM_ASSOCIATED_ROUTE_KEY);
  }

  @Bean
  public Binding userAssociatedFromUserIdSearchBinding(
      @Qualifier(AMQPCommonConfig.USER_EXCHANGE_BEAN) DirectExchange exchange) {
    return BindingBuilder
        .bind(userAssociatedFromUserId())
        .to(exchange)
        .with(AMQPCommonConfig.SEARCH_USER_ASSOCIATED_FROM_USER_ID_ROUTE_KEY);
  }
}
