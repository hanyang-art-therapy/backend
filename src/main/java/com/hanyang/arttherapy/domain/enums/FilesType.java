package com.hanyang.arttherapy.domain.enums;

import lombok.Getter;

@Getter
public enum FilesType {
  ART("art", FileExtension.IMAGE),
  REVIEW("review", FileExtension.IMAGE),
  ;

  private final String directory;
  private final FileExtension allowedFileExtension;

  FilesType(String directory, FileExtension allowedFileExtension) {
    this.directory = directory;
    this.allowedFileExtension = allowedFileExtension;
  }

  public boolean isAllowed(String extension) {
    return allowedFileExtension.isAllowed(extension);
  }
}
