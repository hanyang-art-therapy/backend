package com.hanyang.arttherapy.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.hanyang.arttherapy.domain.ArtArtistRel;
import com.hanyang.arttherapy.domain.Arts;
import com.hanyang.arttherapy.domain.Files;

import lombok.Builder;

public record ArtsListResponseDto(
    Long artsNo, String artName, FileResponseDto files, List<ArtistResponseDto> artists) {

  @Builder
  public static ArtsListResponseDto of(Arts arts, Files file, List<ArtArtistRel> artArtistRels) {
    return new ArtsListResponseDto(
        arts.getArtsNo(),
        arts.getArtName(),
        new FileResponseDto(file.getUrl()),
        artArtistRels.stream()
            .map(
                rel ->
                    new ArtistResponseDto(
                        rel.getArtists().getArtistName(), rel.getArtists().getCohort()))
            .collect(Collectors.toList()));
  }

  public record FileResponseDto(String url) {}

  public record ArtistResponseDto(String artistName, int cohort) {}
}
