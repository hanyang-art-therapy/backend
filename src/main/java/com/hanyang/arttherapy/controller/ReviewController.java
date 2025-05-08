package com.hanyang.arttherapy.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.response.ReviewResponseDto;
import com.hanyang.arttherapy.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/galleries/{galleriesNo}/arts/{artsNo}/reviews")
public class ReviewController {

  private final ReviewService reviewService;

  @GetMapping
  public ResponseEntity<Page<ReviewResponseDto>> getReviews(
      @PathVariable Long galleriesNo,
      @PathVariable Long artsNo,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size) {

    Page<ReviewResponseDto> reviewPage = reviewService.getReviews(artsNo, page, size);
    return ResponseEntity.ok(reviewPage);
  }
}
