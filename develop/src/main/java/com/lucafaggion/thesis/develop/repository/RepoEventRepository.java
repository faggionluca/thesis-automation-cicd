package com.lucafaggion.thesis.develop.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucafaggion.thesis.develop.model.RepoEvent;
import com.lucafaggion.thesis.develop.model.RepoPushEvent;

import io.micrometer.common.lang.NonNull;

public interface RepoEventRepository extends JpaRepository<RepoEvent, Long> {
}
