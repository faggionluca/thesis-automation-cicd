package com.lucafaggion.thesis.repository;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lucafaggion.thesis.common.model.User;

import lombok.NonNull;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(@NonNull String username);

  @Query("SELECT u FROM User u JOIN FETCH u.userAssociatedAccounts uac JOIN FETCH uac.service s WHERE uac.username = :username AND s.name = :serviceName")
  Optional<User> findByUsernameOnService(@Param("username") String username, @Param("serviceName") String serviceName);
}
