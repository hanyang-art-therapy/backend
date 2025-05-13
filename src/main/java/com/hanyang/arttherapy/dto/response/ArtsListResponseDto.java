package com.hanyang.arttherapy.dto.response;

import com.hanyang.arttherapy.domain.Arts;
import com.hanyang.arttherapy.domain.Files;

public record ArtsListResponseDto(
    Long artsNo, String artName, FileResponse files, ArtistResponse artist) {
  // 전체 리스트 조회 전용 dto
  public static ArtsListResponseDto of(Arts arts, Files file, ArtistResponse artistResponse) {
    return new ArtsListResponseDto(
        arts.getArtsNo(), arts.getArtName(), new FileResponse(file.getUrl()), artistResponse);
  }

  public record FileResponse(String url) {}

  public record ArtistResponse(String artistName, int cohort) {}
}
