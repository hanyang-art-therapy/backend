package com.hanyang.arttherapy.service;

import static org.assertj.core.api.Assertions.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.FileSystemExceptionType;
import com.hanyang.arttherapy.domain.enums.*;
import com.hanyang.arttherapy.dto.response.*;
import com.hanyang.arttherapy.repository.*;

@SpringBootTest
class LocalFileStorageServiceTest {

  @Autowired private LocalFileStorageService localFileStorageService;
  @Autowired private FileStorageService fileStorageService;
  @Autowired private FileStorageUtils fileUtils;
  @Autowired FilesRepository fileRepository;

  private MultipartFile multipartFile;

  @BeforeEach
  void init() {
    multipartFile =
        new MockMultipartFile("file", "image.jpg", "image/jpeg", "test content".getBytes());
  }

  @Test
  void storeValidFiles() {

    FilesType fileType = FilesType.ART;

    FileResponseListDto result = fileStorageService.store(List.of(multipartFile), fileType);

    FileResponseDto storedFile = result.files().get(0);

    assertThat(storedFile).isNotNull();
    assertThat(storedFile.url()).contains("art");
    assertThat(storedFile.name()).isNotNull();
    assertThat(storedFile.extension()).isEqualTo("jpg");
    assertThat(storedFile.url()).contains(storedFile.name());

    File savedFile = new File(storedFile.url());
    assertThat(savedFile).exists();
    assertThat(savedFile.isFile()).isTrue();
  }

  @Test
  void invalidFileExtension() {
    FilesType fileType = FilesType.ART;

    MultipartFile invalidFile =
        new MockMultipartFile("file", "invalid.mp4", "video/mp4", "test content".getBytes());

    assertThatThrownBy(() -> fileStorageService.store(List.of(invalidFile), fileType))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(FileSystemExceptionType.INVALID_FILE_EXTENSION.getMessage());
  }
}
