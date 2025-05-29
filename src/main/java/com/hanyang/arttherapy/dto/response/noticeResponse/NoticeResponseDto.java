package com.hanyang.arttherapy.dto.response.noticeResponse;

import java.time.LocalDateTime;

public record NoticeResponseDto(
    Long noticeNo,
    String category,
    String title,
    boolean hasFile,
    int viewCount,
    LocalDateTime createdAt,
    Boolean isFixed) {}
