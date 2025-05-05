package com.hanyang.arttherapy.common.init;

import java.io.File;
import java.nio.file.Paths;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.FileSystemExceptionType;
import com.hanyang.arttherapy.domain.enums.FilesType;

@Component
public class DirectoryInit {

  @Value("${app.storage.path}")
  private String storagePath;

  @PostConstruct
  public void initializeDirectories() {
    for (FilesType fileType : FilesType.values()) {
      String directoryPath = buildDirectoryPath(storagePath, fileType);
      createDirectoryIfNotExist(directoryPath);
    }
  }

  private String buildDirectoryPath(String basePath, FilesType fileType) {
    return Paths.get(basePath, fileType.getDirectory()).toString();
  }

  private void createDirectoryIfNotExist(String directoryPath) {
    File directory = new File(directoryPath);
    if (!directory.exists() && !directory.mkdirs()) {
      throw new CustomException(FileSystemExceptionType.DIRECTORY_CREATION_FAILED);
    }
  }
}
