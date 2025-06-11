package com.hanyang.arttherapy.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponseDto(
    Long reviewsNo,
    String reviewText,
    String userName,
    Long userNo,
    List<FileResponseDto> files,
    LocalDateTime createdAt) {}
