package com.lucafaggion.thesis.develop.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@Configuration
public class TemplateEngineConfig {

  @Bean
  SpringResourceTemplateResolver secondaryTemplateResolver(ApplicationContext applicationContext) {
    SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
    resolver.setApplicationContext(applicationContext);
    resolver.setPrefix("classpath:/configs/");
    resolver.setSuffix(".yaml");
    resolver.setTemplateMode("TEXT");
    resolver.setCharacterEncoding("UTF-8");
    resolver.setOrder(20);
    resolver.setCheckExistence(true);
    return resolver;
  }

  @Bean
  SpringResourceTemplateResolver nonWebTemplateResolver(ApplicationContext applicationContext) {
    SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
    resolver.setApplicationContext(applicationContext);
    resolver.setPrefix("classpath:/templates/");
    resolver.setSuffix(".json");
    resolver.setTemplateMode("JAVASCRIPT");
    resolver.setCharacterEncoding("UTF-8");
    resolver.setOrder(21);
    resolver.setCheckExistence(true);
    return resolver;
  }

  @Bean
  StringTemplateResolver stringTemplateResolver(ApplicationContext applicationContext) {
    StringTemplateResolver resolver = new StringTemplateResolver();
    resolver.setTemplateMode("TEXT");
    resolver.setOrder(30);
    // resolver.setCheckExistence(true);
    return resolver;
  }

}
