package com.lucafaggion.thesis.develop.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.Queue;

@Configuration
public class AMQPClientConfig {
  
}
