package com.hanyang.arttherapy.dto.response;

import java.util.List;

public record FileResponseListDto(List<FileResponseDto> files) {
  public static FileResponseListDto of(List<FileResponseDto> files) {
    return new FileResponseListDto(files);
  }
}
