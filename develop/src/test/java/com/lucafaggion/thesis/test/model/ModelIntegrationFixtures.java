package com.lucafaggion.thesis.test.model;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@Import(AppIntegrationTestConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url= jdbc:postgresql://postdb:5432/testrundb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ModelIntegrationFixtures extends ModelFixtures {

}
