package com.hanyang.arttherapy.dto.response;

import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.domain.enums.FilesType;

public record FileResponseDto(
    Long filesNo, String name, String url, Long filesSize, String extension, FilesType filesType) {
  public static FileResponseDto of(Files file, String url) {
    return new FileResponseDto(
        file.getFilesNo(),
        file.getName(),
        url,
        file.getFilesSize(),
        file.getExtension(),
        file.getFilesType());
  }
}
