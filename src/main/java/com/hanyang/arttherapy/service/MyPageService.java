// MyPageService.java
package com.hanyang.arttherapy.service;

import java.sql.Timestamp;
import java.util.*;

import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.domain.enums.UserStatus;
import com.hanyang.arttherapy.dto.request.MypageEmailRequest;
import com.hanyang.arttherapy.dto.request.userRequest.EmailRequest;
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
  private final HttpSession session;
  private final UserService userService;

  @Transactional(readOnly = true)
  public MyInfoResponseDto getMyInfo(Long userId) {
    Users user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));
    return MyInfoResponseDto.from(user);
  }

  @Transactional
  public String updateUserInfo(
      Long userId, MypageEmailRequest request, String name, String studentNo) {
    Users user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    if (name != null && !name.trim().isEmpty()) {
      user.setUserName(name);
    }

    if (studentNo != null && !studentNo.trim().isEmpty()) {
      boolean studentNoExists = userRepository.existsByStudentNoAndUserNoNot(studentNo, userId);
      if (studentNoExists) {
        throw new CustomException(UserException.STUDENT_ALREADY_EXISTS);
      }
      user.setStudentNo(studentNo);
    }

    if (request.email() != null && !request.email().trim().isEmpty()) {
      boolean emailExists = userRepository.existsByEmailAndUserNoNot(request.email(), userId);
      if (emailExists) {
        throw new CustomException(UserException.EMAIL_ALREADY_EXISTS);
      }

      if (request.verificationCode() == null
          || !isVerifiedEmail(request.email(), request.verificationCode())) {
        throw new CustomException(UserException.EMAIL_VERIFICATION_FAILED);
      }

      user.setEmail(request.email());
    }

    return "회원 정보가 성공적으로 수정되었습니다.";
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

    UsersHistory history =
        usersHistoryRepository
            .findByUser_UserNo(userNo)
            .orElseThrow(() -> new IllegalArgumentException("회원이력이 없습니다"));

    history.setSignoutTimestamp(new Timestamp(System.currentTimeMillis()));
    history.setUserStatus(UserStatus.UNACTIVE);

    return "회원탈퇴 되었습니다.";
  }

  public String checkEmailForChange(String email, Long userNo) {
    Optional<Users> existingUser = userRepository.findByEmail(email);

    if (existingUser.isPresent() && !existingUser.get().getUserNo().equals(userNo)) {
      return "이미 다른 사용자가 사용 중인 이메일입니다.";
    }

    // 🔽 UserService의 public 메서드만 호출
    return userService.checkEmail(new EmailRequest(email));
  }
}
