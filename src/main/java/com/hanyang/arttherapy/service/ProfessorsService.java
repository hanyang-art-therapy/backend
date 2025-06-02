package com.hanyang.arttherapy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.ProfessorExceptionType;
import com.hanyang.arttherapy.common.filter.CustomUserDetail;
import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.domain.Professors;
import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.dto.request.ProfessorsRequestDto;
import com.hanyang.arttherapy.dto.response.ProfessorsResponseDto;
import com.hanyang.arttherapy.repository.FilesRepository;
import com.hanyang.arttherapy.repository.ProfessorsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessorsService {

  private final ProfessorsRepository professorsRepository;
  private final FilesRepository filesRepository;

  // 교수진 전체조회 (누구나 가능)
  public List<ProfessorsResponseDto> getAllProfessors() {
    return professorsRepository.findAll().stream().map(ProfessorsResponseDto::from).toList();
  }

  // 교수진 상세조회 (누구나 가능)
  public ProfessorsResponseDto getProfessorDetail(Long professorNo) {
    Professors professor =
        professorsRepository
            .findById(professorNo)
            .orElseThrow(() -> new CustomException(ProfessorExceptionType.PROFESSOR_NOT_FOUND));

    return ProfessorsResponseDto.from(professor);
  }

  // 교수진 등록 (관리자만)
  @Transactional
  public String saveProfessor(ProfessorsRequestDto requestDto, CustomUserDetail userDetail) {
    checkAdmin(userDetail);

    Files file = null;
    if (requestDto.getFilesNo() != null) {
      file =
          filesRepository
              .findById(requestDto.getFilesNo())
              .orElseThrow(() -> new CustomException(ProfessorExceptionType.FILE_NOT_FOUND));
    }

    Professors professor =
        Professors.builder()
            .professorName(requestDto.getProfessorName())
            .position(requestDto.getPosition())
            .major(requestDto.getMajor())
            .email(requestDto.getEmail())
            .tel(requestDto.getTel())
            .file(file)
            .build();

    professorsRepository.save(professor);
    return "교수 등록에 성공했습니다";
  }

  // 교수진 수정 (관리자만)
  @Transactional
  public String updateProfessor(
      Long professorNo, ProfessorsRequestDto requestDto, CustomUserDetail userDetail) {
    checkAdmin(userDetail);

    Professors professor =
        professorsRepository
            .findById(professorNo)
            .orElseThrow(() -> new CustomException(ProfessorExceptionType.PROFESSOR_NOT_FOUND));

    Files file = null;
    if (requestDto.getFilesNo() != null) {
      file =
          filesRepository
              .findById(requestDto.getFilesNo())
              .orElseThrow(() -> new CustomException(ProfessorExceptionType.FILE_NOT_FOUND));
    }

    professor.updateProfessorIfNotNull(
        requestDto.getProfessorName(),
        requestDto.getPosition(),
        requestDto.getMajor(),
        requestDto.getEmail(),
        requestDto.getTel(),
        file);

    return "교수 정보가 수정되었습니다";
  }

  // 교수진 삭제 (관리자만)
  @Transactional
  public String deleteProfessor(Long professorNo, CustomUserDetail userDetail) {
    checkAdmin(userDetail);

    Professors professor =
        professorsRepository
            .findById(professorNo)
            .orElseThrow(() -> new CustomException(ProfessorExceptionType.PROFESSOR_NOT_FOUND));

    professorsRepository.delete(professor);
    return "교수 정보가 삭제되었습니다";
  }

  // ✅ 관리자 권한 검사 메서드
  private void checkAdmin(CustomUserDetail userDetail) {
    if (userDetail.getUser().getRole() != Role.ADMIN) {
      throw new CustomException(ProfessorExceptionType.UNAUTHORIZED);
    }
  }
}
