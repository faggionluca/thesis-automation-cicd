package com.lucafaggion.thesis.common.config;

import java.io.FileWriter;
import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@AutoConfigurationPackage
public class CommonSpringConfig {
  
  @Bean
  public CommandLineRunner commonInit() throws IOException {
    return args -> {
      System.out.println("Common is getting Initialized");
    };
  }

}
