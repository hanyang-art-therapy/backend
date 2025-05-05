package com.hanyang.arttherapy.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

  @Value("${spring.servlet.multipart.max-file-size}")
  private long maxFileSize;

  @Override
  public FileResponseListDto store(List<MultipartFile> files, FilesType type) {
    List<FileResponseDto> fileResponseDtos =
        files.stream()
            .map(
                file -> {
                  String extension = fileUtils.extractExtension(file.getOriginalFilename());
                  fileUtils.validateFileExtension(type, extension);
                  String savedName = fileUtils.generateUUIDFileName(extension);
                  String filePath = type.getFullPath(storagePath, savedName);

                  fileUtils.saveFile(file, filePath);

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
}
