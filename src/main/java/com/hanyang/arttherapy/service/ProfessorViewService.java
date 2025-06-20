package com.hanyang.arttherapy.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hanyang.arttherapy.dto.response.ProfessorsResponseDto;
import com.hanyang.arttherapy.repository.ProfessorsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessorViewService {

  private final ProfessorsRepository professorsRepository;
  private final FileStorageService fileStorageService;

  // 교수진 정보 조회
  public List<ProfessorsResponseDto> getAllProfessors() {
    return professorsRepository.findAll().stream()
        .map(
            professor -> {
              String fileUrl = null;
              if (professor.getFile() != null) {
                fileUrl = fileStorageService.getFileUrl(professor.getFile().getFilesNo());
              }
              return ProfessorsResponseDto.from(professor, fileUrl);
            })
        .collect(Collectors.toList());
  }
}
