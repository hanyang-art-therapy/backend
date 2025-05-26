package com.hanyang.arttherapy.dto.response.artistResponse;

import com.hanyang.arttherapy.domain.Artists;

public record ArtistResponseDto(String artistName, String studentNo, int cohort) {
  public static ArtistResponseDto of(Artists artist) {
    return new ArtistResponseDto(artist.getArtistName(), artist.getStudentNo(), artist.getCohort());
  }
}
