package com.lucafaggion.thesis.repository;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lucafaggion.thesis.common.model.UserAssociatedAccount;

public interface UserAssociatedAccountRepository extends JpaRepository<UserAssociatedAccount, Long> {

}
