package com.hanyang.arttherapy.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.repository.FilesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileScheduledService {

  private static final int FILE_EXPIRATION_DAYS = 5;
  private final FileStorageService fileStorageService;
  private final FilesRepository filesRepository;

  @Transactional
  @Scheduled(cron = "0 0 3 ? * SUN")
  public void deleteFile() {
    LocalDateTime cutoffDate = getCutoffDate(FILE_EXPIRATION_DAYS);

    List<Files> filesToDelete = findExpiredFilesToDelete(cutoffDate);

    List<Files> unusedFiles = findUnusedFiles(cutoffDate);

    deleteFiles(filesToDelete);
    deleteFiles(unusedFiles);
  }

  private List<Files> findUnusedFiles(LocalDateTime cutoffDate) {
    return filesRepository.findByCreatedAtBeforeAndUseYn(cutoffDate, false);
  }

  private List<Files> findExpiredFilesToDelete(LocalDateTime cutoffDate) {
    return filesRepository.findByCreatedAtBeforeAndDelYn(cutoffDate, true);
  }

  private void deleteFiles(List<Files> files) {
    files.forEach(this::deleteFileFromSystem);
  }

  private void deleteFileFromSystem(Files files) {
    try {
      if (isFileExist(files.getFilesNo())) {
        fileStorageService.deletedFileFromSystem(files.getFilesNo());
        filesRepository.delete(files);
      }
    } catch (Exception e) {
      log.error("파일 삭제 실패, 파일 번호: {}, 오류: {}", files.getFilesNo(), e.getMessage(), e);
    }
  }

  private boolean isFileExist(Long filesNo) {
    Optional<Files> fileOptional = filesRepository.findById(filesNo);
    return fileOptional.isPresent();
  }

  private LocalDateTime getCutoffDate(int days) {
    return LocalDateTime.now().minus(days, ChronoUnit.DAYS);
  }
}
