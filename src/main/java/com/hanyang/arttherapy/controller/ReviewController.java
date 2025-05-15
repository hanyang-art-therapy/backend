package com.hanyang.arttherapy.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.ReviewRequestDto;
import com.hanyang.arttherapy.dto.response.ReviewResponseDto;
import com.hanyang.arttherapy.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/galleries/arts/{artsNo}/reviews")
public class ReviewController {

  private final ReviewService reviewService;

  // 리뷰 조회
  @GetMapping
  public ResponseEntity<Page<ReviewResponseDto>> getReviews(
      @PathVariable Long artsNo, Pageable pageable) {
    return ResponseEntity.ok(reviewService.getReviews(artsNo, pageable));
  }

  // 리뷰 생성
  @PostMapping
  public ResponseEntity<ReviewResponseDto> createReview(
      @PathVariable Long artsNo, @RequestBody ReviewRequestDto reviewRequestDto) {
    ReviewResponseDto responseDto = reviewService.createReview(artsNo, reviewRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  // 리뷰 수정
  @PatchMapping("/{reviewNo}")
  public ResponseEntity<ReviewResponseDto> updateReview(
      @PathVariable Long reviewNo, @RequestBody ReviewRequestDto reviewRequestDto) {
    ReviewResponseDto responseDto =
        reviewService.patchReview(
            reviewNo, reviewRequestDto.reviewText(), reviewRequestDto.filesNo());
    return ResponseEntity.ok(responseDto);
  }

  // 리뷰 삭제
  @DeleteMapping("/{reviewNo}")
  public ResponseEntity<Map<String, String>> deleteReview(@PathVariable Long reviewNo) {
    reviewService.deleteReview(reviewNo);

    Map<String, String> response = Map.of("message", "댓글이 성공적으로 삭제되었습니다.");
    return ResponseEntity.ok(response);
  }
}
