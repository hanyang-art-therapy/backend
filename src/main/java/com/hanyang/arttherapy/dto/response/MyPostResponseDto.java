package com.hanyang.arttherapy.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanyang.arttherapy.domain.Arts;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPostResponseDto {
  private final Long artsNo;
  private final String artName;
  private final String caption;
  private final String artType;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private final LocalDateTime createdAt;

  public static MyPostResponseDto from(Arts art) {
    return MyPostResponseDto.builder()
        .artsNo(art.getArtsNo())
        .artName(art.getArtName())
        .caption(art.getCaption())
        .artType(art.getArtType().name())
        .createdAt(art.getCreatedAt())
        .build();
  }
}
