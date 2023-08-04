package com.lucafaggion.thesis.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucafaggion.thesis.common.model.UserAssociatedAccount;

public interface UserAssociatedAccountRepository extends JpaRepository<UserAssociatedAccount, Long> {
  Optional<UserAssociatedAccount> findByUsernameAndService(String username, String service);
}
