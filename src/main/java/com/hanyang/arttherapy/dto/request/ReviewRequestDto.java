package com.hanyang.arttherapy.dto.request;

import java.util.List;

import com.hanyang.arttherapy.dto.response.FileResponseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {
  private Long userNo;
  private Long artsNo;
  private String reviewText;
  private List<FileResponseDto> files;
}
