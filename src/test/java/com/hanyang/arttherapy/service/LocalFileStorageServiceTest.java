//package com.hanyang.arttherapy.service;
//
//import static org.assertj.core.api.Assertions.*;
//
//import java.io.*;
//import java.util.*;
//
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.hanyang.arttherapy.common.exception.*;
//import com.hanyang.arttherapy.common.exception.exceptionType.*;
//import com.hanyang.arttherapy.domain.*;
//import com.hanyang.arttherapy.domain.enums.*;
//import com.hanyang.arttherapy.dto.response.*;
//import com.hanyang.arttherapy.repository.*;
//
//@SpringBootTest
//@ActiveProfiles("local")
//class LocalFileStorageServiceTest {
//
//  @Autowired private LocalFileStorageService localFileStorageService;
//  @Autowired private FileStorageService fileStorageService;
//  @Autowired private FileStorageUtils fileUtils;
//  @Autowired FilesRepository fileRepository;
//
//  private MultipartFile multipartFile;
//  private Files testFile;
//
//  @BeforeEach
//  void init() {
//    multipartFile =
//        new MockMultipartFile("file", "image.jpg", "image/jpeg", "test content".getBytes());
//  }
//
//  @Test
//  void storeValidFiles() {
//
//    FilesType fileType = FilesType.ART;
//
//    List<FileResponseDto> result = fileStorageService.store(List.of(multipartFile), fileType);
//
//    FileResponseDto storedFile = result.get(0);
//
//    assertThat(storedFile).isNotNull();
//    assertThat(storedFile.url()).contains("art");
//    assertThat(storedFile.name()).isNotNull();
//    assertThat(storedFile.extension()).isEqualTo("jpg");
//    assertThat(storedFile.url()).contains(storedFile.name());
//
//    File savedFile = new File(storedFile.url());
//    assertThat(savedFile).exists();
//    assertThat(savedFile.isFile()).isTrue();
//  }
//
//  @Test
//  void invalidFileExtension() {
//    FilesType fileType = FilesType.ART;
//
//    MultipartFile invalidFile =
//        new MockMultipartFile("file", "invalid.mp4", "video/mp4", "test content".getBytes());
//
//    assertThatThrownBy(() -> fileStorageService.store(List.of(invalidFile), fileType))
//        .isInstanceOf(CustomException.class)
//        .hasMessageContaining(FileSystemExceptionType.INVALID_FILE_EXTENSION.getMessage());
//  }
//
//  @Test
//  @Transactional
//  void softDeleteFile() {
//    createTestFile();
//
//    fileStorageService.softDeleteFile(testFile.getFilesNo());
//
//    Files deletedFile = fileRepository.findById(testFile.getFilesNo()).orElseThrow();
//
//    assertThat(deletedFile.isUseYn()).isFalse();
//    assertThat(deletedFile.isDelYn()).isTrue();
//  }
//
//  private void createTestFile() {
//    testFile =
//        Files.builder()
//            .name("test file.jpg")
//            .url("art/testFile.jpg")
//            .filesSize(12345L)
//            .extension("jpg")
//            .useYn(true)
//            .delYn(false)
//            .build();
//    fileRepository.save(testFile);
//  }
//}
