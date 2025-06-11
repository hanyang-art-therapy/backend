package com.hanyang.arttherapy.dto.response;

import java.util.Map;

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

  private Map<String, Object> files;

  public static ProfessorsResponseDto from(Professors professor, String fileUrl) {
    Map<String, Object> filesMap = null;
    if (professor.getFile() != null) {
      filesMap = Map.of("filesNo", professor.getFile().getFilesNo(), "url", fileUrl);
    }

    return ProfessorsResponseDto.builder()
        .professorNo(professor.getProfessorNo())
        .professorName(professor.getProfessorName())
        .position(professor.getPosition())
        .major(professor.getMajor())
        .email(professor.getEmail())
        .tel(professor.getTel())
        .files(filesMap)
        .build();
  }
}
