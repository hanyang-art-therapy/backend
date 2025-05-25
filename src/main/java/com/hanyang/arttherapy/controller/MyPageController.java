package com.hanyang.arttherapy.controller;

// import com.hanyang.arttherapy.dto.response.MyCommentResponseDto;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.dto.response.MyInfoResponseDto;
import com.hanyang.arttherapy.dto.response.MyPostResponseDto;
import com.hanyang.arttherapy.dto.response.MyReviewResponseDto;
import com.hanyang.arttherapy.repository.ArtsRepository;
import com.hanyang.arttherapy.repository.UserRepository;
import com.hanyang.arttherapy.service.MyPageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my-page")
public class MyPageController {

  private final MyPageService myPageService;
  private final ArtsRepository artsRepository;
  private final UserRepository userRepository;

  // 내 정보 조회
  @GetMapping("/profile")
  public ResponseEntity<MyInfoResponseDto> getMyInfo(@RequestHeader("userId") Long userId) {
    return ResponseEntity.ok(myPageService.getMyInfo(userId));
  }

  // 내 작품 조회
  @GetMapping("/my-posts")
  public ResponseEntity<List<MyPostResponseDto>> getMyPosts(@RequestHeader("userId") Long userId) {
    return ResponseEntity.ok(myPageService.getMyPosts(userId));
  }

  // 내 댓글 조회
  @GetMapping("/my-reviews")
  public ResponseEntity<List<MyReviewResponseDto>> getMyReviews(
      @RequestHeader("userId") Long userId) {
    Users user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    return ResponseEntity.ok(myPageService.getMyReviews(user.getUserNo()));
  }
}
