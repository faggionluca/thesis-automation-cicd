package com.lucafaggion.thesis.develop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucafaggion.thesis.develop.model.RunnerJob;

public interface RunnerJobRepository extends JpaRepository<RunnerJob, Long>{
  
}
