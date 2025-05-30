package com.hanyang.arttherapy.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.domain.Arts;
import com.hanyang.arttherapy.domain.Reviews;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.enums.Role;
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

  // 나의 정보 조회
  @Transactional(readOnly = true)
  public MyInfoResponseDto getMyInfo(Long userId) {
    Users user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));
    return MyInfoResponseDto.from(user);
  }

  // 나의 게시글 조회
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

  // 나의 댓글 조회
  @Transactional(readOnly = true)
  public List<MyReviewResponseDto> getMyReviews(Long userId, String keyword) {
    List<Reviews> reviews;

    if (keyword == null || keyword.trim().isEmpty()) {
      reviews = reviewRepository.findAllByUserNo(userId);
    } else {
      reviews = reviewRepository.findByUserNoAndKeyword(userId, keyword);
    }

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
}
