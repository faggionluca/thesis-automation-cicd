package com.lucafaggion.thesis.develop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${server.port}")
  private String port;
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(request -> request
        .requestMatchers("/webhook/event/**").permitAll()
        .anyRequest().authenticated())
        // Abilitare il csrf ha senso solo in APP che interagiscono con i browser
        // Questo servelet e un resource server che non interagisce con gli utenti ma solo altri server
        .csrf((csrf) -> csrf.disable());
    return http.build();
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedOrigins("http://localhost:" + port)
            .allowedOriginPatterns("http://*.ngrok-free.app");
      }
    };
  }

}
