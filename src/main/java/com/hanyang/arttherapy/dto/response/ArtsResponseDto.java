package com.hanyang.arttherapy.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.hanyang.arttherapy.domain.Arts;

public record ArtsResponseDto(
    Long artsNo,
    String artName,
    String caption,
    String coDescription,
    LocalDateTime createdAt,
    List<ArtArtistRelResponseDto> artists,
    FileResponseDto file,
    String galleriesTitle,
    LocalDate galleriesStartDate) {
  public static ArtsResponseDto of(
      Arts arts,
      LocalDateTime createdAt,
      List<ArtArtistRelResponseDto> artists,
      FileResponseDto file) {
    return new ArtsResponseDto(
        arts.getArtsNo(),
        arts.getArtName(),
        arts.getCaption(),
        arts.getCoDescription(),
        createdAt,
        artists,
        file,
        arts.getGalleries().getTitle(),
        arts.getGalleries().getStartDate());
  }
}
