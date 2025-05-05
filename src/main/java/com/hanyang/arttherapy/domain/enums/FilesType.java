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

  public String getFullPath(String basePath, String fileName) {
    return basePath + "/" + directory + "/" + fileName;
  }

  public boolean isAllowed(String extension) {
    return allowedFileExtension.isAllowed(extension);
  }
}

@Getter
enum FileExtension {
  IMAGE(
      "jpg", "jpeg", "png", "gif", "bmp", "tiff", "tif", "webp", "svg", "heif", "heic", "raw",
      "nef", "cr2", "arw", "dng", "orf", "sr2", "ico", "eps", "psd"),
  DOCUMENT("pdf", "docx", "xlsx", "pptx", "txt", "rtf", "odt"),
  VIDEO("mp4", "mkv", "avi", "mov", "flv", "webm"),
  AUDIO("mp3", "wav", "ogg", "flac", "aac", "m4a"),
  ARCHIVE("zip", "rar", "tar", "7z", "gzip");

  private final String[] extensions;

  FileExtension(String... extensions) {
    this.extensions = extensions;
  }

  public boolean isAllowed(String extension) {
    for (String ext : extensions) {
      if (ext.equalsIgnoreCase(extension)) {
        return true;
      }
    }
    return false;
  }
}
