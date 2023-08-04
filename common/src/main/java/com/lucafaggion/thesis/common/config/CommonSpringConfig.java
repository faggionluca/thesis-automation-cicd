package com.lucafaggion.thesis.common.config;

import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
