package com.lucafaggion.thesis.develop.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.lucafaggion.thesis.develop.service.APIInterceptor;

@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate(RabbitTemplate template) {
    RestTemplate restTemplate = new RestTemplate(
        new BufferingClientHttpRequestFactory(
            new SimpleClientHttpRequestFactory()));

    List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
    if (CollectionUtils.isEmpty(interceptors)) {
      interceptors = new ArrayList<>();
    }
    interceptors.add(new APIInterceptor(template));
    restTemplate.setInterceptors(interceptors);
    return restTemplate;
  }

}
