package com.hanyang.arttherapy.dto.response;

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
    FileResponseDto file) {
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
        file);
  }
}
