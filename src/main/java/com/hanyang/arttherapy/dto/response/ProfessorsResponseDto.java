package com.hanyang.arttherapy.dto.response;

import com.hanyang.arttherapy.domain.Professors;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfessorsResponseDto {
  private Long professorNo;
  private String professorName;
  private String position;
  private String major;
  private String email;
  private String tel;
  private String fileUrl; // 파일 URL만 필요하면

  public static ProfessorsResponseDto from(Professors professor) {
    return ProfessorsResponseDto.builder()
        .professorNo(professor.getProfessorNo())
        .professorName(professor.getProfessorName())
        .position(professor.getPosition())
        .major(professor.getMajor())
        .email(professor.getEmail())
        .tel(professor.getTel())
        .fileUrl(professor.getFile() != null ? professor.getFile().getUrl() : null)
        .build();
  }
}
