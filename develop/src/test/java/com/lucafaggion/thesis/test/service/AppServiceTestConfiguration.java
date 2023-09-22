package com.lucafaggion.thesis.test.service;

import java.math.BigInteger;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.lucafaggion.thesis.common.config.AMQPCommonConfig;
import com.lucafaggion.thesis.common.message.SearchUserAssociatedByUserAndService;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;

/*
 * Configurazione per testare i Service
 */
@EnableAutoConfiguration
@SpringBootConfiguration
@Import(AMQPCommonConfig.class)
@EntityScan(basePackages = "com.lucafaggion.thesis.develop")
@EnableJpaRepositories(basePackages = "com.lucafaggion.thesis.develop")
@ComponentScan(basePackages = { "com.lucafaggion.thesis.develop", "com.lucafaggion.thesis.common.config" })
public class AppServiceTestConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppServiceTestConfiguration.class);

  /**
   * Mock per le Queue di RabbitMQ (org.springframework.amqp)
   */
  @Configuration
  @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(name = AMQPCommonConfig.USER_EXCHANGE), value = @Queue, key = AMQPCommonConfig.USER_ROUTE_KEY))
  public class Config {

    @RabbitHandler
    public UserAssociatedAccount searchUserAssociatedAccount(
        SearchUserAssociatedByUserAndService searchUserAssociatedByUserAndService) {
      long neverExpire = System.currentTimeMillis() + 3600000;
      if (searchUserAssociatedByUserAndService.getId().equals(BigInteger.valueOf(1))
          && searchUserAssociatedByUserAndService.getServiceName().equals("github")) {
        return UserAssociatedAccount.builder()
            .username("faggionluca")
            .id(1)
            .token("ghu_NlKm0QV3HJZcVB9s6Kc6GEDPqT6fHq48Sxe9") //Fake token
            .refresh_token("ghu_NlKm0QV3HJZcVB9s6Kc6GEDPqT6fHq48Sxe9") //Fake refresh_token
            .token_valid_until(new Date(neverExpire))
            .refresh_token_valid_until(new Date(neverExpire))
            .build();
      }
      return null;
    }
  }

  // @EventListener
  // public void handleContextRefresh(ContextRefreshedEvent event) {
  //     final Environment env = event.getApplicationContext()
  //                                  .getEnvironment();

  //     LOGGER.info("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));

  //     final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();

  //     StreamSupport.stream(sources.spliterator(), false)
  //                  .filter(ps -> ps instanceof EnumerablePropertySource)
  //                  .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
  //                  .flatMap(Arrays::stream)
  //                  .distinct()
  //                  .filter(prop -> !(prop.contains("credentials") || prop.contains("password")))
  //                  .forEach(prop -> LOGGER.info("{}: {}", prop, env.getProperty(prop)));
  // }

}
