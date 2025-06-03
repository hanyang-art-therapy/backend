package com.hanyang.arttherapy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.common.filter.CustomUserDetail;
import com.hanyang.arttherapy.dto.request.MypageUpdateRequest;
import com.hanyang.arttherapy.dto.request.userRequest.EmailRequest;
import com.hanyang.arttherapy.dto.response.MyInfoResponseDto;
import com.hanyang.arttherapy.dto.response.MyPostResponseDto;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.service.MyPageService;
import com.hanyang.arttherapy.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-page")
public class MyPageController {

  private final MyPageService myPageService;
  private final UserService userService;

  // 내 정보 조회
  @GetMapping("/profile")
  public ResponseEntity<MyInfoResponseDto> getMyInfo(
      @AuthenticationPrincipal CustomUserDetail userDetails) {
    Long userId = userDetails.getUser().getUserNo();
    return ResponseEntity.ok(myPageService.getMyInfo(userId));
  }

  // 내 정보 수정 (이름 + 학번 + 이메일)
  @PatchMapping("/profile")
  public ResponseEntity<MyInfoResponseDto> updateMyInfo(
      @AuthenticationPrincipal CustomUserDetail userDetails,
      @RequestBody MypageUpdateRequest request) {
    Long userId = userDetails.getUser().getUserNo();
    MyInfoResponseDto updatedInfo = myPageService.updateUserInfo(userId, request);
    return ResponseEntity.ok(updatedInfo);
  }

  // 이메일 인증 요청 (이메일 변경용)
  //  @PostMapping("/email-verification")
  //  public ResponseEntity<CommonMessageResponse> verifyEmailForChange(
  //      @RequestBody MypageEmailRequest request,
  //      @AuthenticationPrincipal CustomUserDetail userDetails) {
  //    Long userNo = userDetails.getUser().getUserNo();
  //    String message = myPageService.checkEmailForChange(request.email(), userNo);
  //    return ResponseEntity.ok(new CommonMessageResponse(message));
  //  }

  @PostMapping("/email-verification")
  public ResponseEntity<CommonMessageResponse> verifyEmailForChange(
      @RequestBody MypageUpdateRequest request) {
    String message = userService.checkEmail(new EmailRequest(request.email()));
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  // 내 작품 조회
  @GetMapping("/my-posts")
  public ResponseEntity<List<MyPostResponseDto>> getMyPosts(
      @AuthenticationPrincipal CustomUserDetail userDetails) {
    Long userId = userDetails.getUser().getUserNo();
    return ResponseEntity.ok(myPageService.getMyPosts(userId));
  }

  // 내 댓글 조회
  @GetMapping("/my-reviews")
  public ResponseEntity<Map<String, Object>> getMyReviews(
      @AuthenticationPrincipal CustomUserDetail userDetails,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(required = false) String keyword) {
    Long userId = userDetails.getUser().getUserNo();
    return ResponseEntity.ok(myPageService.getMyReviews(userId, keyword, page, size));
  }

  // 회원탈퇴
  @PatchMapping("/withdraw")
  public ResponseEntity<CommonMessageResponse> withdraw(
      @AuthenticationPrincipal CustomUserDetail userDetails) {
    Long userId = userDetails.getUser().getUserNo();
    String message = myPageService.withdrawByUserNo(userId);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }
}
