package com.hanyang.arttherapy.dto.request;

import java.time.LocalDate;

import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.domain.Users;

import lombok.Getter;

@Getter
public class GalleriesRequestDto {

  private final String title;
  private final LocalDate startDate;
  private final LocalDate endDate;

  public GalleriesRequestDto(String title, LocalDate startDate, LocalDate endDate) {
    this.title = title;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public Galleries toEntity(Users user) {
    return Galleries.builder()
        .title(title)
        .startDate(startDate)
        .endDate(endDate)
        .user(user)
        .build();
  }
}
