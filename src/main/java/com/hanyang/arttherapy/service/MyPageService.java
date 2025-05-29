package com.hanyang.arttherapy.service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.domain.Arts;
import com.hanyang.arttherapy.domain.Reviews;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.UsersHistory;
import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.domain.enums.UserStatus;
import com.hanyang.arttherapy.dto.response.MyInfoResponseDto;
import com.hanyang.arttherapy.dto.response.MyPostResponseDto;
import com.hanyang.arttherapy.dto.response.MyReviewResponseDto;
import com.hanyang.arttherapy.repository.ArtsRepository;
import com.hanyang.arttherapy.repository.ReviewRepository;
import com.hanyang.arttherapy.repository.UserRepository;
import com.hanyang.arttherapy.repository.UsersHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final UserRepository userRepository;
  private final UsersHistoryRepository usersHistoryRepository;
  private final ArtsRepository artsRepository;
  private final ReviewRepository reviewRepository;

  @Transactional(readOnly = true)
  public MyInfoResponseDto getMyInfo(Long userId) {
    Users user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));
    return MyInfoResponseDto.from(user);
  }

  @Transactional(readOnly = true)
  public List<MyPostResponseDto> getMyPosts(Long userId) {
    Users user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    if (user.getRole() != Role.ARTIST) {
      return Collections.emptyList();
    }

    List<Arts> arts = artsRepository.findAllByStudentNo(user.getStudentNo());
    return arts.stream().map(MyPostResponseDto::from).toList();
  }

  @Transactional(readOnly = true)
  public List<MyReviewResponseDto> getMyReviews(Long userNo) {
    List<Reviews> reviews = reviewRepository.findAllByUserNo(userNo);

    return reviews.stream()
        .map(
            r ->
                MyReviewResponseDto.builder()
                    .reviewNo(r.getReviewsNo())
                    .artsNo(r.getArts() != null ? r.getArts().getArtsNo() : null)
                    .artName(r.getArts() != null ? r.getArts().getArtName() : null)
                    .reviewText(r.getReviewText())
                    .createdAt(r.getCreatedAt())
                    .build())
        .toList();
  }

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
