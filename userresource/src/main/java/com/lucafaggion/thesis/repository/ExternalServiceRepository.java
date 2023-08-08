package com.lucafaggion.thesis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucafaggion.thesis.common.model.ExternalService;

public interface ExternalServiceRepository extends JpaRepository<ExternalService, Long>{
  Optional<ExternalService> findByName(String name);
}
