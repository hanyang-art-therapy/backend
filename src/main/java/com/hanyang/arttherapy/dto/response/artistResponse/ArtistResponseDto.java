package com.hanyang.arttherapy.dto.response.artistResponse;

import com.hanyang.arttherapy.domain.Artists;

public record ArtistResponseDto(Long artistNo, String artistName, String studentNo, int cohort) {
  public static ArtistResponseDto of(Artists artist) {
    return new ArtistResponseDto(
        artist.getArtistNo(), artist.getArtistName(), artist.getStudentNo(), artist.getCohort());
  }
}
