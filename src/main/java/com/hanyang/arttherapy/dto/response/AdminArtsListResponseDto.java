package com.hanyang.arttherapy.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminArtsListResponseDto {
  private Long artsNo;
  private String artName;
  private Long galleriesNo;
  private String galleriesTitle;
  private List<String> artists;
}
