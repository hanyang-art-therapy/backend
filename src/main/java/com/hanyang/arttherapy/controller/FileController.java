package com.hanyang.arttherapy.controller;

import java.util.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.hanyang.arttherapy.domain.enums.*;
import com.hanyang.arttherapy.dto.response.*;
import com.hanyang.arttherapy.service.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("files")
@RequiredArgsConstructor
public class FileController {

  private final FileStorageService fileStorageService;

  @PostMapping
  public ResponseEntity<List<FileResponseDto>> store(
      @RequestPart("files") List<MultipartFile> files,
      @RequestParam("filesType") FilesType filesType) {
    return ResponseEntity.ok(fileStorageService.store(files, filesType));
  }

  @DeleteMapping("/{filesNo}")
  public ResponseEntity<Void> softDelete(@PathVariable Long filesNo) {
    fileStorageService.softDeleteFile(filesNo);
    return ResponseEntity.noContent().build();
  }
}
