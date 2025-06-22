package com.hanyang.arttherapy.domain.enums;

import java.util.*;

import lombok.Getter;

@Getter
public enum FileExtension {
  IMAGE(
      "jpg", "jpeg", "png", "gif", "bmp", "tiff", "tif", "webp", "svg", "heif", "heic", "raw",
      "nef", "cr2", "arw", "dng", "orf", "sr2", "ico", "eps", "psd"),
  DOCUMENT("pdf", "docx", "xlsx", "pptx", "txt", "rtf", "odt", "hwp", "hwpx", "ppt", "doc", "xls"),
  VIDEO("mp4", "mkv", "avi", "mov", "flv", "webm"),
  AUDIO("mp3", "wav", "ogg", "flac", "aac", "m4a"),
  ARCHIVE("zip", "rar", "tar", "7z", "gzip"),
  ALL();

  private String[] extensions;

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

  static {
    List<String> all = new ArrayList<>();
    for (FileExtension fe : values()) {
      if (fe != ALL) {
        Collections.addAll(all, fe.extensions);
      }
    }
    ALL.extensions = all.toArray(new String[0]);
  }
}
