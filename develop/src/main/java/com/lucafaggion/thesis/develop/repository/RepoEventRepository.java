package com.lucafaggion.thesis.develop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucafaggion.thesis.develop.model.RepoEvent;

public interface RepoEventRepository extends JpaRepository<RepoEvent, Long> {
}
