package com.lucafaggion.thesis.test.service;

import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.lucafaggion.thesis.test.UnitTestFixtures;

@Import(AppServiceTestConfiguration.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url= jdbc:postgresql://postdb:5432/testrundb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ServiceIntegrationFixtures extends UnitTestFixtures {
  
  /**
   * Utilizzabile per il debug, mettedo un breakpoint nel proprio IDE
   */
  @AfterAll
  static void afterAllDebug() {
    System.out.println("AfterAllDebug");
  }
}
