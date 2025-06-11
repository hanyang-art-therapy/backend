package com.hanyang.arttherapy.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.hanyang.arttherapy.domain.ArtArtistRel;
import com.hanyang.arttherapy.domain.Arts;

public record ArtsListResponseDto(
    Long artsNo,
    String artName,
    String coDescription,
    FileResponseDto files,
    List<ArtistResponseDto> artists) {

  public static ArtsListResponseDto of(
      Arts arts, String name, String url, List<ArtArtistRel> artArtistRels) {
    String coDescription = arts.getCoDescription(); // 공동 작품 설명 (공동 작품이 아닐 경우 null/빈 문자열로 저장돼 있음)

    List<ArtistResponseDto> artistDtos =
        artArtistRels.stream()
            .map(
                rel ->
                    new ArtistResponseDto(
                        rel.getArtists().getArtistName(),
                        rel.getArtists().getCohort(),
                        rel.getDescription()))
            .collect(Collectors.toList());

    return new ArtsListResponseDto(
        arts.getArtsNo(),
        arts.getArtName(),
        coDescription,
        new FileResponseDto(name, url),
        artistDtos);
  }

  public record FileResponseDto(String name, String url) {}

  public record ArtistResponseDto(String artistName, int cohort, String description) {}
}
