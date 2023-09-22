package com.lucafaggion.thesis.test.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import com.lucafaggion.thesis.develop.config.AppConfig;
import com.lucafaggion.thesis.develop.model.ServiceProprieties;

@SpringBootTest(classes = { AppConfig.class, ServiceProprieties.class })
@EnableAutoConfiguration(exclude = { JpaRepositoriesAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
    SecurityAutoConfiguration.class })
public class ServiceProprietiesTest {

  @Autowired
  ServiceProprieties serviceProprieties;

  @Test
  void shouldCorretlySet() {
    System.out.print(serviceProprieties);
  }

}
