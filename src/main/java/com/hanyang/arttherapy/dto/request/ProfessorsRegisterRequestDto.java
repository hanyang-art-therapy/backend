package com.hanyang.arttherapy.dto.request;

import lombok.Getter;

@Getter
public class ProfessorsRegisterRequestDto {

  private String professorName;
  private String position;
  private String major;
  private String email;
  private String tel;
  private Long filesNo; // 프로필 사진 ID (nullable)
}
