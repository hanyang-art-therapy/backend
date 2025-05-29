package com.hanyang.arttherapy.dto.response.noticeResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.hanyang.arttherapy.dto.response.FileResponseDto;

public record NoticeDetailResponseDto(
    Long noticeNo,
    String title,
    String category,
    LocalDateTime createdAt,
    LocalDate periodStart,
    LocalDate periodEnd,
    int viewCount,
    String content,
    List<FileResponseDto> files,
    AdjacentNoticeDto previous,
    AdjacentNoticeDto next,
    Boolean isFixed) {}
