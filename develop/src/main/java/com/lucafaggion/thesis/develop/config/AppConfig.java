package com.lucafaggion.thesis.develop.config;

import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

@Configuration
// https://www.jvt.me/posts/2022/01/10/jackson-yaml-json/
@Import(JacksonAutoConfiguration.class)
public class AppConfig {

  @Bean("yamlObjectMapper")
  public YAMLMapper yamlObjectMapper() {
    YAMLMapper mapper = new YAMLMapper();
    mapper.registerModule(new Jdk8Module());
    return mapper;
  }
}
