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
    resolver.setOrder(2);
    resolver.setCheckExistence(true);
    return resolver;
  }

  @Bean
  StringTemplateResolver stringTemplateResolver() {
    StringTemplateResolver resolver = new StringTemplateResolver();
    resolver.setTemplateMode("TEXT");
    resolver.setOrder(3);
    resolver.setCheckExistence(true);
    return resolver;
  }

}
