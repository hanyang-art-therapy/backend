package com.hanyang.arttherapy.service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hanyang.arttherapy.common.exception.*;
import com.hanyang.arttherapy.common.exception.exceptionType.*;
import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.domain.enums.*;
import com.hanyang.arttherapy.dto.response.*;
import com.hanyang.arttherapy.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileStorageService {

  private final FilesRepository filesRepository;

  @Value("${app.storage.path}")
  private String storagePath;

  @Value("${spring.servlet.multipart.max-file-size}")
  private long maxFileSize;

  public FileResponseListDto store(List<MultipartFile> files, FilesType type) {
    List<FileResponseDto> fileResponseDtos =
        files.stream()
            .map(
                file -> {
                  String extension = extractExtension(file.getOriginalFilename());
                  validateFileExtension(type, extension);
                  String savedName = generateUUIDFileName(extension);
                  String filePath = type.getFullPath(storagePath, savedName);

                  saveFile(file, filePath);

                  Files filesEntity = convertToEntity(file, type, savedName, filePath, extension);
                  filesRepository.save(filesEntity);

                  return FileResponseDto.of(filesEntity);
                })
            .collect(Collectors.toList());

    return FileResponseListDto.of(fileResponseDtos);
  }

  private Files convertToEntity(
      MultipartFile file, FilesType type, String savedName, String filePath, String extension) {
    return Files.builder()
        .name(savedName)
        .url(filePath)
        .filesSize(file.getSize())
        .extension(extension)
        .filesType(type)
        .build();
  }

  // 파일 저장
  private void saveFile(MultipartFile file, String filePath) {
    try {
      file.transferTo(new File(filePath));
    } catch (IOException e) {
      throw new CustomException(FileSystemExceptionType.FILE_SAVE_FAILED);
    }
  }

  // 확장자 검증
  private void validateFileExtension(FilesType type, String extension) {
    if (!type.isAllowed(extension)) {
      throw new CustomException(FileSystemExceptionType.INVALID_FILE_EXTENSION);
    }
  }

  // 확장자 추출
  private String extractExtension(String originFilename) {
    int dotIndex = originFilename.lastIndexOf(".");
    if (dotIndex == -1) {
      throw new CustomException(FileSystemExceptionType.FILE_EXTENSION_MISSING);
    }
    return originFilename.substring(dotIndex + 1);
  }

  // UUID 파일명 생성
  private String generateUUIDFileName(String extension) {
    return UUID.randomUUID().toString() + "." + extension;
  }
}
