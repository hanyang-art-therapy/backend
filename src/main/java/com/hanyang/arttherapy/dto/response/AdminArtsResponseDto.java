package com.hanyang.arttherapy.dto.response;

import java.time.LocalDateTime;

import com.hanyang.arttherapy.domain.Arts;
import com.hanyang.arttherapy.domain.enums.ArtType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminArtsResponseDto {

  private final Long artsNo;
  private final Long galleriesNo;
  private final Long filesNo;
  private final String artName;
  private final String caption;
  private final ArtType artType;
  private final LocalDateTime uploadedAt;

  public static AdminArtsResponseDto from(Arts arts) {
    return AdminArtsResponseDto.builder()
        .artsNo(arts.getArtsNo())
        .galleriesNo(arts.getGalleriesNo())
        .filesNo(arts.getFilesNo())
        .artName(arts.getArtName())
        .caption(arts.getCaption())
        .artType(arts.getArtType())
        .uploadedAt(arts.getCreatedAt())
        .build();
  }
}
