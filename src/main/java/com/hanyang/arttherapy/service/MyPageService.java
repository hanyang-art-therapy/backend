package com.hanyang.arttherapy.service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
  public Map<String, Object> getMyReviews(Long userId, String keyword, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    Page<Reviews> pageResult = reviewRepository.findByUserAndKeyword(userId, keyword, pageable);

    List<MyReviewResponseDto> content =
        pageResult.stream()
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

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("content", content);
    result.put("page", page); // 요청받은 page 그대로 반환 (1-based)
    result.put("size", size);
    result.put("totalElements", pageResult.getTotalElements());
    result.put("totalPages", pageResult.getTotalPages());

    return result;
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
