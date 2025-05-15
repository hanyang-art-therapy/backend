package com.hanyang.arttherapy.service;

import java.sql.Timestamp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.UsersHistory;
import com.hanyang.arttherapy.domain.enums.UserStatus;
import com.hanyang.arttherapy.repository.UserRepository;
import com.hanyang.arttherapy.repository.UsersHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final UserRepository userRepository;
  private final UsersHistoryRepository usersHistoryRepository;

  @Transactional
  public String withdrawByUserNo(Long userNo) {
    Users user =
        userRepository
            .findById(userNo)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    // 상태 변경
    user.setUserStatus(UserStatus.UNACTIVE);

    // 기존 이력 조회 후 탈회 및 상태 변경
    UsersHistory history =
        usersHistoryRepository
            .findByUser_UserNo(userNo)
            .orElseThrow(() -> new IllegalArgumentException("회원이력이 없습니다"));

    history.setSignoutTimestamp(new Timestamp(System.currentTimeMillis()));
    history.setUserStatus(UserStatus.UNACTIVE);
    return "회원탈퇴 되었습니다.";
  }
}
