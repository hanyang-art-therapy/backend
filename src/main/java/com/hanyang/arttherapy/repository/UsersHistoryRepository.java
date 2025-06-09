package com.hanyang.arttherapy.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanyang.arttherapy.domain.UsersHistory;
import com.hanyang.arttherapy.domain.enums.UserStatus;

public interface UsersHistoryRepository extends JpaRepository<UsersHistory, Long> {
  Optional<UsersHistory> findByUser_UserNo(Long userNo);

  List<UsersHistory> findByUserStatusAndBannedTimestampBefore(
      UserStatus userStatus, Timestamp timestamp);
}
