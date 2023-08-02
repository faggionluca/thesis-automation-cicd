package com.lucafaggion.thesis.develop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucafaggion.thesis.develop.model.Repo;

public interface RepoRepository extends JpaRepository<Repo, Long> {
  List<Repo> findByOwner(Long user_id);
}
