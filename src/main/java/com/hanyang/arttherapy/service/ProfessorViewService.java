package com.hanyang.arttherapy.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hanyang.arttherapy.domain.Professors;
import com.hanyang.arttherapy.dto.response.ProfessorsResponseDto;
import com.hanyang.arttherapy.repository.ProfessorsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessorViewService {

  private final ProfessorsRepository professorsRepository;

  // 교수진 정보 소개
  public List<ProfessorsResponseDto> getAllProfessors() {
    List<Professors> professors = professorsRepository.findAll();

    return professors.stream()
        .map(
            professor ->
                ProfessorsResponseDto.from(
                    professor, professor.getFile() != null ? professor.getFile().getUrl() : null))
        .collect(Collectors.toList());
  }
}
