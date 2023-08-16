package com.lucafaggion.thesis.develop.service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import com.lucafaggion.thesis.common.config.AMQPCommonConfig;
import com.lucafaggion.thesis.common.message.SearchUserAssociatedByUserAndService;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;

/*
 * Configurazione per testare i Service
 * https://docs.spring.io/spring-amqp/docs/current/reference/html/testing.html#test-harness
 */
@EnableAutoConfiguration
@SpringBootConfiguration
@Import(AMQPCommonConfig.class)
public class AppServiceTestConfiguration {
  
  @Configuration
  public class Config {

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

    @RabbitListener(queues=AMQPCommonConfig.SEARCH_USER_ASSOCIATED_FROM_USER_ID_ROUTE_KEY)
    public UserAssociatedAccount searchUserAssociatedAccount(SearchUserAssociatedByUserAndService searchUserAssociatedByUserAndService) {
        return UserAssociatedAccount.builder()
        .username("test username")
        .build();
    }
  }
}
