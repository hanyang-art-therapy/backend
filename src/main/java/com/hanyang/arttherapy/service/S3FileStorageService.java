package com.hanyang.arttherapy.service;

import java.io.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.*;
import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.domain.enums.*;
import com.hanyang.arttherapy.dto.response.*;
import com.hanyang.arttherapy.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;

@Slf4j
@Service
@Profile("!local")
@Transactional
@RequiredArgsConstructor
public class S3FileStorageService implements FileStorageService {

  private final S3Client s3Client;
  private final FilesRepository filesRepository;
  private final FileStorageUtils fileUtils;

  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucket;

  @Value("${spring.cloud.aws.cloudfront.url}")
  private String cloudFrontUrl;

  @Override
  public List<FileResponseDto> store(List<MultipartFile> files, FilesType type) {
    return files.stream()
        .map(
            file -> {
              String extension = fileUtils.getValidFileExtension(type, file.getOriginalFilename());
              String savedName = fileUtils.generateUUIDFileName(extension);
              String s3Key = fileUtils.getS3Path(type, savedName);

              uploadFileToS3(file, s3Key);

              Files fileEntity = convertToEntity(file, type, savedName, s3Key, extension);
              filesRepository.save(fileEntity);

              String fileUrl = fileUtils.getCloudFrontFileUrl(cloudFrontUrl, s3Key);
              return FileResponseDto.of(fileEntity, fileUrl);
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
    String s3Key = extractS3Key(file.getUrl());
    deleteFileFromS3(s3Key);
  }

  @Override
  public String getFileUrl(Long filesNo) {
    Files file = getFileById(filesNo);
    return fileUtils.getCloudFrontFileUrl("https://" + cloudFrontUrl, file.getUrl());
  }

  private Files getFileById(Long filesNo) {
    return filesRepository
        .findById(filesNo)
        .orElseThrow(() -> new CustomException(FileSystemExceptionType.FILE_NOT_FOUND));
  }

  private void uploadFileToS3(MultipartFile file, String fileName) {
    try {
      s3Client.putObject(
          createPutObjectRequest(file, fileName), RequestBody.fromBytes(file.getBytes()));
    } catch (IOException e) {
      log.error("S3 파일 업로드 중 오류 발생: {}", e.getMessage(), e);
      throw new CustomException(FileSystemExceptionType.FILE_UPLOAD_FAILED);
    }
  }

  private void deleteFileFromS3(String s3Key) {
    try {
      s3Client.deleteObject(deleteObjectRequest -> deleteObjectRequest.bucket(bucket).key(s3Key));
      log.info("S3 파일 삭제 완료: {}", s3Key);
    } catch (Exception e) {
      log.error("S3 파일 삭제 실패: {}", s3Key, e);
      throw new CustomException(FileSystemExceptionType.FILE_DELETE_FAILED);
    }
  }

  private PutObjectRequest createPutObjectRequest(MultipartFile file, String fileName) {
    return PutObjectRequest.builder()
        .bucket(bucket)
        .key(fileName)
        .contentType(file.getContentType())
        .build();
  }

  private String extractS3Key(String url) {
    return url.replace(cloudFrontUrl + "/", "");
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
