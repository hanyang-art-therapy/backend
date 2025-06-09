package com.hanyang.arttherapy.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminArtsDetailResponseDto {
  private Long artsNo;
  private String artName;
  private String caption;
  private String artType;
  private String fileUrl;
  private String coDescription;

  private Long galleriesNo;
  private String title;

  private List<ArtistInfo> artists;

  @Data
  @Builder
  public static class ArtistInfo {
    private Long artistNo;
    private String name;
    private String description; // art_artist_rel.description
  }
}
