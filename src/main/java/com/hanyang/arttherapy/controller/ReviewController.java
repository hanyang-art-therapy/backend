package com.hanyang.arttherapy.controller;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.ReviewRequestDto;
import com.hanyang.arttherapy.dto.request.admin.AdminBanRequest;
import com.hanyang.arttherapy.dto.response.ReviewResponseDto;
import com.hanyang.arttherapy.dto.response.noticeResponse.CommonDataResponse;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.service.AdminUserService;
import com.hanyang.arttherapy.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/galleries/arts/{artsNo}/reviews")
public class ReviewController {

  private final ReviewService reviewService;
  private final AdminUserService adminUserService;

  // 리뷰 조회
  @GetMapping
  public ResponseEntity<Map<String, Object>> getReviews(
      @PathVariable Long artsNo,
      @PageableDefault(size = 4, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(reviewService.getReviews(artsNo, pageable));
  }

  // 리뷰 생성
  @PostMapping
  public ResponseEntity<CommonDataResponse<ReviewResponseDto>> createReview(
      @PathVariable Long artsNo, @RequestBody ReviewRequestDto reviewRequestDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(reviewService.createReview(artsNo, reviewRequestDto));
  }

  // 리뷰 수정
  @PatchMapping("/{reviewNo}")
  public ResponseEntity<CommonDataResponse<ReviewResponseDto>> updateReview(
      @PathVariable Long reviewNo, @RequestBody ReviewRequestDto reviewRequestDto) {
    return ResponseEntity.ok(
        reviewService.patchReview(
            reviewNo, reviewRequestDto.reviewText(), reviewRequestDto.filesNo()));
  }

  // 리뷰 삭제
  @DeleteMapping("/{reviewNo}")
  public ResponseEntity<CommonMessageResponse> deleteReview(@PathVariable Long reviewNo) {
    return ResponseEntity.ok(reviewService.deleteReview(reviewNo));
  }

  // 리뷰 정지
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{reviewNo}/ban")
  public ResponseEntity<CommonMessageResponse> bannedReview(@RequestBody AdminBanRequest request) {
    String message = adminUserService.bannedReview(request);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }
}
