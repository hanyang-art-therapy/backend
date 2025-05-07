package com.hanyang.arttherapy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.hanyang.arttherapy.domain.Users;

public interface UserRepository
    extends JpaRepository<Users, Long>, QuerydslPredicateExecutor<Users> {
  boolean existsByUserId(String userId);

  boolean existsByEmail(String email);

  boolean existsByStudentNo(String studentNo);
}
