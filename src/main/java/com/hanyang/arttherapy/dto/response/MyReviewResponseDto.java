package com.hanyang.arttherapy.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyReviewResponseDto {
  private Long reviewsNo; // 기존 reviewId → reviewsNo 이름 변경
  private Long artsNo; // artsNo 추가
  private String artName; // 작품 이름
  private String reviewText; // content → reviewText로 변경
  private LocalDateTime createdAt;
}
