package com.hanyang.arttherapy.service;

import java.util.*;

import org.springframework.web.multipart.MultipartFile;

import com.hanyang.arttherapy.domain.enums.*;
import com.hanyang.arttherapy.dto.response.*;

public interface FileStorageService {
  List<FileResponseDto> store(List<MultipartFile> files, FilesType type);

  void softDeleteFile(Long filesNo);

  void deletedFileFromSystem(Long filesNo);

  String getFileUrl(Long filesNo);
}
