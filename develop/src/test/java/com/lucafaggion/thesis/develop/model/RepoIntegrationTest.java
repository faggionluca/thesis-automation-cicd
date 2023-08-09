package com.lucafaggion.thesis.develop.model;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.util.Assert;

import com.lucafaggion.thesis.develop.repository.RepoRepository;

@DataJpaTest
public class RepoIntegrationTest extends ModelIntegrationFixtures {
  @Autowired
  RepoRepository repoRepository;

  @Test
  void repoSaveAndIsFoundInSet() {
    // Stiamo testando se la funzione hashCode non muta dopo il salvataggio

    Set<Repo> set = new HashSet<>();

    Repo testRepositorySave = Repo.builder()
        .url("http://test.test")
        .build();

    set.add(testRepositorySave);
    repoRepository.save(testRepositorySave);

    Assert.isTrue(set.contains(testRepositorySave), "Entity not found in the set");
  }
}
