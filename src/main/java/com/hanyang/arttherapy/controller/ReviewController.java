package com.hanyang.arttherapy.controller;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.ReviewRequestDto;
import com.hanyang.arttherapy.dto.response.ReviewResponseDto;
import com.hanyang.arttherapy.dto.response.noticeResponse.CommonDataResponse;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/galleries/arts/{artsNo}/reviews")
public class ReviewController {

  private final ReviewService reviewService;

  // 리뷰 조회
  @GetMapping
  public ResponseEntity<Map<String, Object>> getReviews(
      @PathVariable Long artsNo, @PageableDefault(size = 5) Pageable pageable) {
    return ResponseEntity.ok(reviewService.getReviews(artsNo, pageable));
  }

  // 리뷰 생성
  @PostMapping
  public ResponseEntity<CommonDataResponse<ReviewResponseDto>> createReview(
      @PathVariable Long artsNo, @RequestBody ReviewRequestDto reviewRequestDto) {

    ReviewResponseDto responseDto = reviewService.createReview(artsNo, reviewRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CommonDataResponse<>("댓글 등록이 완료되었습니다", responseDto));
  }

  // 리뷰 수정
  @PatchMapping("/{reviewNo}")
  public ResponseEntity<CommonDataResponse<ReviewResponseDto>> updateReview(
      @PathVariable Long reviewNo, @RequestBody ReviewRequestDto reviewRequestDto) {

    ReviewResponseDto responseDto =
        reviewService.patchReview(
            reviewNo, reviewRequestDto.reviewText(), reviewRequestDto.filesNo());
    return ResponseEntity.ok(new CommonDataResponse<>("댓글 수정이 완료되었습니다", responseDto));
  }

  // 리뷰 삭제
  @DeleteMapping("/{reviewNo}")
  public ResponseEntity<CommonMessageResponse> deleteReview(@PathVariable Long reviewNo) {
    reviewService.deleteReview(reviewNo);
    return ResponseEntity.ok(new CommonMessageResponse("댓글 삭제가 완료되었습니다."));
  }
}
