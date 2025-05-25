package com.hanyang.arttherapy.dto.response;

import com.hanyang.arttherapy.domain.ArtArtistRel;

public record ArtArtistRelResponseDto(
    String artistName, String studentNo, int cohort, String description) {
  public static ArtArtistRelResponseDto of(ArtArtistRel rel) {
    return new ArtArtistRelResponseDto(
        rel.getArtists().getArtistName(),
        rel.getArtists().getStudentNo(),
        rel.getArtists().getCohort(),
        rel.getDescription());
  }
}
