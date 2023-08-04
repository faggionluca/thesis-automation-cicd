package com.lucafaggion.thesis.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucafaggion.thesis.common.model.User;

import lombok.NonNull;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(@NonNull String username);
}
