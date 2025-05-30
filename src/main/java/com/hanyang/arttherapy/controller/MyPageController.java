package com.hanyang.arttherapy.controller;

// import com.hanyang.arttherapy.dto.response.MyCommentResponseDto;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.common.filter.CustomUserDetail;
import com.hanyang.arttherapy.dto.response.MyInfoResponseDto;
import com.hanyang.arttherapy.dto.response.MyPostResponseDto;
import com.hanyang.arttherapy.repository.ArtsRepository;
import com.hanyang.arttherapy.repository.UserRepository;
import com.hanyang.arttherapy.service.MyPageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-page")
public class MyPageController {

  private final MyPageService myPageService;
  private final ArtsRepository artsRepository;
  private final UserRepository userRepository;

  // 내 정보 조회
  @GetMapping("/profile")
  public ResponseEntity<MyInfoResponseDto> getMyInfo(
      @AuthenticationPrincipal CustomUserDetail userDetails) {
    Long userId = userDetails.getUser().getUserNo();
    return ResponseEntity.ok(myPageService.getMyInfo(userId));
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
}
