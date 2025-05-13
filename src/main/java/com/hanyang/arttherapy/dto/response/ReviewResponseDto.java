package com.hanyang.arttherapy.dto.response;

import java.util.List;

public record ReviewResponseDto(
    Long reviewNo, String reviewText, String userName, List<FileResponseDto> files) {

  public static ReviewResponseDto of(
      Long reviewNo, String reviewText, String userName, List<FileResponseDto> files) {
    return new ReviewResponseDto(reviewNo, reviewText, userName, files);
  }
}
