package com.lucafaggion.thesis.develop.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "com.lucafaggion.thesis.config") 
public class ServiceProprieties {

  private Integer poolSize; 
}
