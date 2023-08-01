package com.lucafaggion.thesis.develop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;

public interface RunnerTaskConfigRepository extends JpaRepository<RunnerTaskConfig, Long> {
  
}
