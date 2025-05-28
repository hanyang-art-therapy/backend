package com.hanyang.arttherapy.dto.response.artistResponse;

import java.util.List;

public record ArtistResponseListDto(List<ArtistResponseDto> artists) {
  public static ArtistResponseListDto of(List<ArtistResponseDto> artists) {
    return new ArtistResponseListDto(artists);
  }
}
