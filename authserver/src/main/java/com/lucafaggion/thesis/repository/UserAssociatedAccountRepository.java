package com.lucafaggion.thesis.repository;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lucafaggion.thesis.common.model.UserAssociatedAccount;

public interface UserAssociatedAccountRepository extends JpaRepository<UserAssociatedAccount, Long> {
  
  @Query("SELECT uac FROM User u JOIN u.userAssociatedAccounts uac JOIN uac.service s WHERE u.id = :user_id AND s.name = :serviceName")
  Optional<UserAssociatedAccount> findByUserIdAndServiceName(@Param("user_id") BigInteger user_id, @Param("serviceName") String serviceName);
}
