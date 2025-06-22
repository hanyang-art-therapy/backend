package com.hanyang.arttherapy.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
enum FileExtension {
  IMAGE(
      "jpg", "jpeg", "png", "gif", "bmp", "tiff", "tif", "webp", "svg", "heif", "heic", "raw",
      "nef", "cr2", "arw", "dng", "orf", "sr2", "ico", "eps", "psd"),
  DOCUMENT("pdf", "docx", "xlsx", "pptx", "txt", "rtf", "odt", "hwp", "hwpx", "ppt", "doc", "xls"),
  VIDEO("mp4", "mkv", "avi", "mov", "flv", "webm"),
  AUDIO("mp3", "wav", "ogg", "flac", "aac", "m4a"),
  ARCHIVE("zip", "rar", "tar", "7z", "gzip"),
  ALL(combineAll());

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

  private static String[] combineAll() {
    List<String> all = new ArrayList<>();
    for (FileExtension fe : values()) {
      if (fe != ALL) {
        Collections.addAll(all, fe.extensions);
      }
    }
    return all.toArray(new String[0]);
  }
}
