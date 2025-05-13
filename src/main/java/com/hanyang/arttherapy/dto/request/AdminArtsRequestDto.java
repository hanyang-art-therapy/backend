package com.hanyang.arttherapy.dto.request;

import java.util.List;

import com.hanyang.arttherapy.domain.enums.ArtType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminArtsRequestDto {

  private final Long galleriesNo;
  private final Long filesNo;
  private final String artName;
  private final String caption;
  private final ArtType artType;
  private final List<ArtistRelDto> artistList;

  @Builder
  public AdminArtsRequestDto(
      Long galleriesNo,
      Long filesNo,
      String artName,
      String caption,
      ArtType artType,
      List<ArtistRelDto> artistList) {
    this.galleriesNo = galleriesNo;
    this.filesNo = filesNo;
    this.artName = artName;
    this.caption = caption;
    this.artType = artType;
    this.artistList = artistList;
  }

  @Getter
  public static class ArtistRelDto {
    private final Long artistId;
    private final String description;

    @Builder
    public ArtistRelDto(Long artistId, String description) {
      this.artistId = artistId;
      this.description = description;
    }
  }
}
