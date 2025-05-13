package com.hanyang.arttherapy.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GalleriesRequestDto {

  private Long userNo; // 사용자 번호 (연관관계)
  private String title; // 전시회 제목
  private LocalDateTime startDate;
  private LocalDateTime endDate;
}
