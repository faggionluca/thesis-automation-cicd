package com.lucafaggion.thesis.config;

import org.springframework.context.annotation.Configuration;

/**
 * Classe di configurazione delle Queue di Spring AMQP 
 */
@Configuration
public class AMQPServerConfig {
  
  /*
   * Queue USER_SEARCH_QUEUE per eseguire query sugli utenti
   */
  public static final String USER_SEARCH_QUEUE = "q.user-search.request";

  /*
   * Queue EXTERNAL_SERVICE per eseguire comandi API legati agli External Service Services
   */
  public static final String EXTERNAL_SERVICE_QUEUE = "q.external-service.request";

}
