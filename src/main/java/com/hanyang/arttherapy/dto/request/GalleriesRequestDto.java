package com.hanyang.arttherapy.dto.request;

import java.time.LocalDateTime;

import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.domain.Users;

import lombok.Getter;

@Getter
public class GalleriesRequestDto {

  private final String title;
  private final LocalDateTime startDate;
  private final LocalDateTime endDate;

  public GalleriesRequestDto(String title, LocalDateTime startDate, LocalDateTime endDate) {
    this.title = title;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public Galleries toEntity(Users user) {
    return Galleries.builder().title(title).startDate(startDate).endDate(endDate).build();
  }
}
