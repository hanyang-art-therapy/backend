package com.hanyang.arttherapy.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.common.filter.CustomUserDetail;
import com.hanyang.arttherapy.domain.Reviews;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.UsersHistory;
import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.domain.enums.UserStatus;
import com.hanyang.arttherapy.dto.request.admin.AdminBanRequest;
import com.hanyang.arttherapy.dto.request.users.UserRequestDto;
import com.hanyang.arttherapy.dto.response.userResponse.UserDetailDto;
import com.hanyang.arttherapy.dto.response.userResponse.UserDto;
import com.hanyang.arttherapy.repository.ReviewRepository;
import com.hanyang.arttherapy.repository.UserRepository;
import com.hanyang.arttherapy.repository.UsersHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

  private final UserRepository userRepository;
  private final UsersHistoryRepository usersHistoryRepository;
  private final ReviewRepository reviewRepository;

  // 전체 조회 또는 이름 검색 조회 (무한스크롤)
  public Map<String, Object> getUsers(String userName, Long lastId) {
    getAdminUserOrThrow();
    Long cursor = (lastId == null) ? Long.MAX_VALUE : lastId;

    List<Users> users =
        (userName != null && !userName.isBlank())
            ? userRepository.findTop10ByUserNameContainingAndUserNoLessThanOrderByUserNoDesc(
                userName, cursor)
            : userRepository.findTop10ByUserNoLessThanOrderByUserNoDesc(cursor);

    List<UserDto> content = users.stream().map(this::convertToDto).toList();

    boolean hasNext = content.size() == 10;

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("content", content);
    response.put("lastId", !content.isEmpty() ? content.get(content.size() - 1).userNo() : null);
    response.put("hasNext", hasNext);
    return response;
  }

  public UserDetailDto getUserDetail(Long userNo) {
    getAdminUserOrThrow();
    Users user =
        userRepository
            .findByUserNo(userNo)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    UsersHistory history =
        usersHistoryRepository
            .findByUser_UserNo(userNo)
            .orElseThrow(() -> new CustomException(UserException.USER_HISTORY_NOT_FOUND));

    return new UserDetailDto(
        user.getUserNo(),
        user.getUserId(),
        user.getEmail(),
        user.getUserName(),
        user.getStudentNo(),
        user.getRole(),
        user.getUserStatus(),
        history.getSigninTimestamp(),
        history.getSignoutTimestamp(),
        history.getBannedTimestamp(),
        history.getCause());
  }

  public String updateUser(Long userNo, UserRequestDto request) {
    getAdminUserOrThrow();
    Users user =
        userRepository
            .findByUserNo(userNo)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    user.updateInfo(
        request.email(),
        request.userName(),
        request.studentNo(),
        request.role(),
        request.userStatus());

    return "회원 정보가 수정되었습니다.";
  }

  private UserDto convertToDto(Users user) {
    return new UserDto(
        user.getUserNo(),
        user.getUserName(),
        user.getStudentNo(),
        user.getRole(),
        user.getUserStatus());
  }

  private Users getAdminUserOrThrow() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new CustomException(UserException.UNAUTHENTICATED);
    }

    Object principal = auth.getPrincipal();
    if (principal instanceof CustomUserDetail customUserDetail) {
      Users user = customUserDetail.getUser();
      if (!user.getRole().equals(Role.ADMIN)) {
        throw new CustomException(UserException.NOT_ADMIN);
      }
      return user;
    }
    throw new CustomException(UserException.UNAUTHENTICATED);
  }

  // 부적절 댓글 회원 정지
  @Transactional
  public String bannedReview(AdminBanRequest request) {
    // 리뷰 조회
    Reviews review =
        reviewRepository
            .findById(request.reviewNo())
            .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

    // 리뷰 작성자 조회
    Users user = review.getUser();

    // 이미 정지된 경우
    if (user.getUserStatus() == UserStatus.BANNED) {
      return "이미 정지된 사용자입니다.";
    }

    // Users 테이블 업데이트
    user.setUserStatus(UserStatus.BANNED);
    userRepository.save(user);

    // UsersHistory에 정지 기록 저장
    UsersHistory history =
        usersHistoryRepository
            .findByUser_UserNo(user.getUserNo())
            .orElseThrow(() -> new RuntimeException("히스토리 없음"));

    // 기존 기록 업데이트
    history.setUserStatus(UserStatus.BANNED);
    history.setBannedTimestamp(Timestamp.valueOf(LocalDateTime.now()));
    history.setCause(request.cause());

    usersHistoryRepository.save(history);

    return "해당 리뷰 작성자를 정지시켰습니다.";
  }
}
