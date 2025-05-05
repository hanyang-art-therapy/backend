package com.hanyang.arttherapy.domain.enums;

import lombok.Getter;

@Getter
public enum FilesType {
  ART("art"),
  REVIEW("review"),
  ;

  private final String directory;

  FilesType(String directory) {
    this.directory = directory;
  }

  public String getFullPath(String basePath, String fileName) {
    return basePath + "/" + directory + "/" + fileName;
  }
}
