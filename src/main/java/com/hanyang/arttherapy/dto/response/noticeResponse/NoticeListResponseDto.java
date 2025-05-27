package com.hanyang.arttherapy.dto.response.noticeResponse;

import java.util.List;

public record NoticeListResponseDto(
    List<NoticeResponseDto> content,
    int page,
    int size,
    int totalElements,
    int totalPages,
    boolean isLast) {}
