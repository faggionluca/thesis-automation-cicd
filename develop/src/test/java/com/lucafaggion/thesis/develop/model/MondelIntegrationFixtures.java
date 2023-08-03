package com.lucafaggion.thesis.develop.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;

import com.lucafaggion.thesis.develop.repository.RepoRepository;

import jakarta.transaction.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url= jdbc:postgresql://postdb:5432/testrundb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class MondelIntegrationFixtures extends ModelFixtures {

}
