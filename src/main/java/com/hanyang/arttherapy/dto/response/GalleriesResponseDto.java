package com.hanyang.arttherapy.dto.response;

import java.time.LocalDate;

import com.hanyang.arttherapy.domain.Galleries;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GalleriesResponseDto {

  private final Long galleriesNo;
  private final String title;
  private final LocalDate startDate;
  private final LocalDate endDate;

  public static GalleriesResponseDto from(Galleries gallery) {
    return GalleriesResponseDto.builder()
        .galleriesNo(gallery.getGalleriesNo())
        .title(gallery.getTitle())
        .startDate(gallery.getStartDate())
        .endDate(gallery.getEndDate())
        .build();
  }
}
