package com.hanyang.arttherapy.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.ProfessorExceptionType;
import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.domain.Professors;
import com.hanyang.arttherapy.dto.request.ProfessorsRegisterRequestDto;
import com.hanyang.arttherapy.repository.FilesRepository;
import com.hanyang.arttherapy.repository.ProfessorsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessorsService {

  private final ProfessorsRepository professorsRepository;
  private final FilesRepository filesRepository;

  // 교수진 전체 조회

  // 교수진 상세조회

  // 교수진 등록
  @Transactional
  public void registerProfessor(ProfessorsRegisterRequestDto request, Long userId) {
    Files file = null;
    if (request.getFilesNo() != null) {
      file =
          filesRepository
              .findById(request.getFilesNo())
              .orElseThrow(() -> new CustomException(ProfessorExceptionType.FILE_NOT_FOUND));
    }

    Professors professor =
        Professors.builder()
            .professorName(request.getProfessorName())
            .position(request.getPosition())
            .major(request.getMajor())
            .email(request.getEmail())
            .tel(request.getTel())
            .file(file)
            .build();

    professorsRepository.save(professor);
  }

  // 교수진 수정

  // 교수진 삭제
}
