package com.hanyang.arttherapy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanyang.arttherapy.domain.UsersHistory;

public interface UsersHistoryRepository extends JpaRepository<UsersHistory, Long> {
  Optional<UsersHistory> findByUser_UserNo(Long userNo);
}
