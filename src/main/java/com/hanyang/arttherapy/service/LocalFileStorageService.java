package com.hanyang.arttherapy.service;

import java.io.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.*;
import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.domain.enums.*;
import com.hanyang.arttherapy.dto.response.*;
import com.hanyang.arttherapy.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("local")
@Transactional
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

  private final FilesRepository filesRepository;
  private final FileStorageUtils fileUtils;

  @Value("${app.storage.path}")
  private String storagePath;

  @Override
  public List<FileResponseDto> store(List<MultipartFile> files, FilesType type) {
    return files.stream()
        .map(
            file -> {
              String extension = fileUtils.getValidFileExtension(type, file.getOriginalFilename());
              String savedName = fileUtils.generateUUIDFileName(extension);
              String filePath = fileUtils.getLocalPath(type, savedName, storagePath);

              fileUtils.saveFile(file, filePath);

              Files fileEntity = convertToEntity(file, type, savedName, filePath, extension);
              filesRepository.save(fileEntity);

              return FileResponseDto.of(fileEntity, filePath);
            })
        .toList();
  }

  @Override
  public void softDeleteFile(Long filesNo) {
    Files file = getFileById(filesNo);
    file.markAsDeleted();
    filesRepository.save(file);
  }

  @Override
  public void deletedFileFromSystem(Long filesNo) {
    Files file = getFileById(filesNo);
    File fileToDelete = new File(file.getUrl());
    boolean deleted = fileToDelete.delete();
    if (!deleted) {
      throw new CustomException(FileSystemExceptionType.FILE_DELETE_FAILED);
    }
  }

  private Files getFileById(Long filesNo) {
    return filesRepository
        .findById(filesNo)
        .orElseThrow(() -> new CustomException(FileSystemExceptionType.FILE_NOT_FOUND));
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
}
