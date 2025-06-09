package com.hanyang.arttherapy.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.domain.enums.UserStatus;
import com.hanyang.arttherapy.dto.request.MypageUpdateRequest;
import com.hanyang.arttherapy.dto.response.MyInfoResponseDto;
import com.hanyang.arttherapy.dto.response.MyPostResponseDto;
import com.hanyang.arttherapy.dto.response.MyReviewResponseDto;
import com.hanyang.arttherapy.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final UserRepository userRepository;
  private final UsersHistoryRepository usersHistoryRepository;
  private final ArtsRepository artsRepository;
  private final ReviewRepository reviewRepository;
  private final HttpSession session;
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional(readOnly = true)
  public MyInfoResponseDto getMyInfo(Long userNo, String userId) {
    Users user =
        userRepository
            .findById(userNo)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    return MyInfoResponseDto.builder()
        .userId(userId)
        .email(user.getEmail())
        .userName(user.getUserName())
        .role(user.getRole())
        .studentNo(user.getStudentNo())
        .build();
  }

  @Transactional
  public MyInfoResponseDto updateUserInfo(Long userId, MypageUpdateRequest request) {
    Users user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    // 학번 중복 체크
    if (request.studentNo() != null && !request.studentNo().trim().isEmpty()) {
      boolean studentNoExists =
          userRepository.existsByStudentNoAndUserNoNot(request.studentNo(), userId);
      if (studentNoExists) {
        throw new CustomException(UserException.STUDENT_ALREADY_EXISTS);
      }
    }

    // 이메일 중복 및 인증 확인
    if (request.email() != null && !request.email().trim().isEmpty()) {
      boolean emailExists = userRepository.existsByEmailAndUserNoNot(request.email(), userId);
      if (emailExists) {
        throw new CustomException(UserException.EMAIL_ALREADY_EXISTS);
      }

      if (request.verificationCode() == null
          || !isVerifiedEmail(request.email(), request.verificationCode())) {
        throw new CustomException(UserException.EMAIL_VERIFICATION_FAILED);
      }
    }

    // role 변경
    Role updatedRole;
    if (request.studentNo() != null && !request.studentNo().trim().isEmpty()) {
      updatedRole = Role.ARTIST;
    } else {
      updatedRole = Role.USER;
    }

    user.updateInfo(
        request.email() != null ? request.email() : user.getEmail(),
        request.userName() != null ? request.userName() : user.getUserName(),
        request.studentNo() != null ? request.studentNo() : null,
        updatedRole,
        user.getUserStatus());

    return MyInfoResponseDto.from(user, user.getUserId());
  }

  private boolean isVerifiedEmail(String email, String code) {
    String storedCode = (String) session.getAttribute("verificationCode");
    Long expirationTime = (Long) session.getAttribute("verificationCodeExpirationTime");

    if (storedCode == null || expirationTime == null) return false;
    if (System.currentTimeMillis() > expirationTime) return false;

    return storedCode.equals(code);
  }

  @Transactional(readOnly = true)
  public List<MyPostResponseDto> getMyPosts(Long userId) {
    Users user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    if (user.getRole() != Role.ARTIST) return Collections.emptyList();

    List<Arts> arts = artsRepository.findAllByStudentNo(user.getStudentNo());
    return arts.stream().map(MyPostResponseDto::from).toList();
  }

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
    result.put("page", page);
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

    user.setUserStatus(UserStatus.UNACTIVE);
    user.setEmail("");
    user.setPassword("");

    UsersHistory history =
        usersHistoryRepository
            .findByUser_UserNo(userNo)
            .orElseThrow(() -> new CustomException(UserException.USER_HISTORY_NOT_FOUND));

    history.setSignoutTimestamp();
    history.setUserStatus(UserStatus.UNACTIVE);

    List<RefreshTokens> tokens = refreshTokenRepository.findAllByUsers_UserNo(userNo);
    tokens.forEach(refreshTokenRepository::delete);

    return "회원탈퇴 되었습니다.";
  }
}
