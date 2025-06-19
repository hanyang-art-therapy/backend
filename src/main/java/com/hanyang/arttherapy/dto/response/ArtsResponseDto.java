package com.hanyang.arttherapy.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.hanyang.arttherapy.domain.Arts;

public record ArtsResponseDto(
    Long artsNo,
    String artName,
    String caption,
    String coDescription,
    List<ArtArtistRelResponseDto> artists,
    FileResponseDto file,
    String title,
    LocalDate startDate) {
  public static ArtsResponseDto of(
      Arts arts, List<ArtArtistRelResponseDto> artists, FileResponseDto file) {
    return new ArtsResponseDto(
        arts.getArtsNo(),
        arts.getArtName(),
        arts.getCaption(),
        arts.getCoDescription(),
        artists,
        file,
        arts.getGalleries().getTitle(),
        arts.getGalleries().getStartDate());
  }
}
