package com.hanyang.arttherapy.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminArtsListResponseDto {
  private Long artsNo;
  private String artName;
  private String artType;
  private Long galleriesNo;
  private String galleriesTitle;
}
