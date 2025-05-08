package com.hanyang.arttherapy.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtsDetailResponseDto {
  private Long artsNo;
  private String artName;
  private String caption;
  private String description;
  private String uploadedAt;
  private GallerySimpleDto galleries;
  private List<ArtistResponseDto> artists;
  private List<FileResponseDto> files;
  private List<ReviewResponseDto> reviews;
}
