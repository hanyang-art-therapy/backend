package com.hanyang.arttherapy.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ArtsResponseDto(
    Long artsNo,
    String artName,
    String caption,
    String description,
    LocalDateTime createdAt,
    List<ArtistResponseDto> artists,
    FileResponseDto file) {
  public static ArtsResponseDto of(
      Long artsNo,
      String artName,
      String caption,
      String description,
      LocalDateTime createdAt,
      List<ArtistResponseDto> artists,
      FileResponseDto file) {
    return new ArtsResponseDto(artsNo, artName, caption, description, createdAt, artists, file);
  }
}
