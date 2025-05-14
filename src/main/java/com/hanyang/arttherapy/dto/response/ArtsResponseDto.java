package com.hanyang.arttherapy.dto.response;

import java.util.List;

public record ArtsResponseDto(
    Long artsNo,
    String artName,
    String caption,
    String description,
    String createdAt,
    GalleryResponseDto gallery,
    List<ArtistResponseDto> artists,
    FileResponseDto file,
    List<ReviewResponseDto> reviews) {
  public static ArtsResponseDto of(
      Long artsNo,
      String artName,
      String caption,
      String description,
      String createdAt,
      GalleryResponseDto gallery,
      List<ArtistResponseDto> artists,
      FileResponseDto file,
      List<ReviewResponseDto> reviews) {
    return new ArtsResponseDto(
        artsNo, artName, caption, description, createdAt, gallery, artists, file, reviews);
  }
}
