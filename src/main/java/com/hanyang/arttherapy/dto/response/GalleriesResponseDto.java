package com.hanyang.arttherapy.dto.response;

import java.time.LocalDateTime;

import com.hanyang.arttherapy.domain.Galleries;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GalleriesResponseDto {
  private Long galleriesNo;
  private String title;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Long userNo;

  public static GalleriesResponseDto from(Galleries galleries) {
    return GalleriesResponseDto.builder()
        .galleriesNo(galleries.getGalleriesNo())
        .title(galleries.getTitle())
        .startDate(galleries.getStartDate())
        .endDate(galleries.getEndDate())
        .userNo(galleries.getUser().getUserNo())
        .build();
  }
}
