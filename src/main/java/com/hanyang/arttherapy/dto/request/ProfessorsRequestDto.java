package com.hanyang.arttherapy.dto.request;

import lombok.Getter;

@Getter
public class ProfessorsRequestDto {
  private String professorName;
  private String position;
  private String major;
  private String email;
  private String tel;
  private Long filesNo; // ✅ 단일 파일만 업로드하므로 filesNo로 받음
}
