package com.hanyang.arttherapy.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.domain.Arts;
import com.hanyang.arttherapy.domain.Review;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.domain.enums.UserStatus;
import com.hanyang.arttherapy.dto.response.MyInfoResponseDto;
import com.hanyang.arttherapy.dto.response.MyPostResponseDto;
import com.hanyang.arttherapy.dto.response.MyReviewResponseDto;
import com.hanyang.arttherapy.repository.ArtsRepository;
import com.hanyang.arttherapy.repository.ReviewRepository;
import com.hanyang.arttherapy.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final UserRepository userRepository;
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

  // 탈퇴
  @Transactional
  public void withdrawByUserNo(Long userNo) {
    Users user =
        userRepository
            .findById(userNo)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    user.setUserStatus(UserStatus.UNACTIVE); // 상태 변경만
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

  public List<MyReviewResponseDto> getMyReviews(Long userNo) {
    List<Review> reviews = reviewRepository.findAllByUser_UserNo(userNo);

    return reviews.stream()
        .map(
            r ->
                MyReviewResponseDto.builder()
                    .reviewNo(r.getReviewNo())
                    .artsNo(r.getArts().getArtsNo())
                    .artName(r.getArts().getArtName())
                    .reviewText(r.getReviewText())
                    .createdAt(r.getCreatedAt())
                    .build())
        .toList();
  }
}
