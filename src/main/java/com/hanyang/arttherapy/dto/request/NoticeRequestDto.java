package com.hanyang.arttherapy.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.hanyang.arttherapy.domain.enums.NoticeCategory;

public record NoticeRequestDto(
    String title,
    NoticeCategory category,
    LocalDate periodStart,
    LocalDate periodEnd,
    String content,
    List<Long> filesNo,
    Boolean isFixed) {}
