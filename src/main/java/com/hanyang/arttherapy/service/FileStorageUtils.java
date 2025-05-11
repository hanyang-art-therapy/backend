package com.hanyang.arttherapy.service;

import java.io.*;
import java.util.*;

import org.springframework.stereotype.*;
import org.springframework.web.multipart.MultipartFile;

import com.hanyang.arttherapy.common.exception.*;
import com.hanyang.arttherapy.common.exception.exceptionType.*;
import com.hanyang.arttherapy.domain.enums.*;

@Service
public class FileStorageUtils {

  // 파일 저장
  public void saveFile(MultipartFile file, String filePath) {
    try {
      file.transferTo(new File(filePath));
    } catch (IOException e) {
      throw new CustomException(FileSystemExceptionType.FILE_SAVE_FAILED);
    }
  }

  public String getValidFileExtension(FilesType type, String extension) {
    String extention = extractExtension(extension);
    validateFileExtension(type, extention);
    return extention;
  }

  // 확장자 검증
  public void validateFileExtension(FilesType type, String extension) {
    if (!type.isAllowed(extension)) {
      throw new CustomException(FileSystemExceptionType.INVALID_FILE_EXTENSION);
    }
  }

  // 확장자 추출
  public String extractExtension(String originFilename) {
    int dotIndex = originFilename.lastIndexOf(".");
    if (dotIndex == -1) {
      throw new CustomException(FileSystemExceptionType.FILE_EXTENSION_MISSING);
    }
    return originFilename.substring(dotIndex + 1);
  }

  // UUID 파일명 생성
  public String generateUUIDFileName(String extension) {
    return UUID.randomUUID().toString() + "." + extension;
  }
}
