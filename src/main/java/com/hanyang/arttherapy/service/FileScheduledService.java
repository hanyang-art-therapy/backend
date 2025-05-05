package com.hanyang.arttherapy.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.repository.FilesRepository;

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

    List<Files> oldFiles =
        filesRepository.findByCreatedAtBeforeAndDelYnAndUseYn(cutoffDate, true, false);

    oldFiles.forEach(
        file -> {
          fileStorageService.deletedFileFromSystem(file);
          filesRepository.delete(file);
        });
  }

  private LocalDateTime getCutoffDate(int days) {
    return LocalDateTime.now().minus(days, ChronoUnit.DAYS);
  }
}
