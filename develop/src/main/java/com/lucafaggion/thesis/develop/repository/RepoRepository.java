package com.lucafaggion.thesis.develop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucafaggion.thesis.develop.model.Repo;

public interface RepoRepository extends JpaRepository<Repo, Long> {
  Optional<Repo> findByUrl(String url);
}
