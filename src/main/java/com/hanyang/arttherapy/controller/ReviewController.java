package com.hanyang.arttherapy.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.hanyang.arttherapy.dto.request.ReviewRequestDto;
import com.hanyang.arttherapy.dto.response.ReviewResponseDto;
import com.hanyang.arttherapy.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/galleries/{galleriesNo}/arts/{artsNo}/reviews")
public class ReviewController {

  private final ReviewService reviewService;

  // 리뷰 조회
  @GetMapping
  public ResponseEntity<Page<ReviewResponseDto>> getReviews(
      @PathVariable Long galleriesNo, @PathVariable Long artsNo, Pageable pageable) {
    Page<ReviewResponseDto> reviewPage = reviewService.getReviews(artsNo, pageable);
    return ResponseEntity.ok(reviewPage);
  }

  // 리뷰 생성
  @PostMapping
  public ResponseEntity<ReviewResponseDto> createReview(
      @PathVariable Long galleriesNo,
      @PathVariable Long artsNo,
      @ModelAttribute ReviewRequestDto reviewRequest) {
    ReviewResponseDto response = reviewService.createReview(galleriesNo, artsNo, reviewRequest);
    return ResponseEntity.status(201).body(response);
  }

  // 리뷰 수정
  @PatchMapping("/{reviewNo}")
  public ResponseEntity<ReviewResponseDto> patchReview(
      @PathVariable Long galleriesNo,
      @PathVariable Long artsNo,
      @PathVariable Long reviewNo,
      @ModelAttribute Map<String, Object> request) {

    String reviewText = (String) request.get("reviewText");
    List<MultipartFile> files = (List<MultipartFile>) request.get("files");

    ReviewResponseDto responseDto = reviewService.patchReview(reviewNo, reviewText, files);
    return ResponseEntity.ok(responseDto);
  }

  // 리뷰 삭제
  @DeleteMapping("/{reviewNo}")
  public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Long reviewNo) {
    reviewService.deleteReview(reviewNo);

    Map<String, Object> response = new HashMap<>();
    response.put("message", "댓글이 성공적으로 삭제되었습니다.");

    return ResponseEntity.ok(response);
  }
}
