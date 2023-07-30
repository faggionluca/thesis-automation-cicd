package com.lucafaggion.thesis.develop.model;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
  "spring.datasource.url= jdbc:postgresql://postdb:5432/testrundb",
  "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class MondelIntegrationFixtures extends ModelFixtures {
  
}
