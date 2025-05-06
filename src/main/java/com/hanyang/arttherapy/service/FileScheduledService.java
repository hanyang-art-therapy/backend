package com.hanyang.arttherapy.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.*;

import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileScheduledService {

  private final LocalFileStorageService fileStorageService;
  private final FilesRepository filesRepository;

  private static final int FILE_EXPIRATION_DAYS = 5;

  @Scheduled(cron = "0 0 0 * * ?")
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
    fileStorageService.deletedFileFromSystem(files);
    filesRepository.delete(files);
  }

  private LocalDateTime getCutoffDate(int days) {
    return LocalDateTime.now().minus(days, ChronoUnit.DAYS);
  }
}
