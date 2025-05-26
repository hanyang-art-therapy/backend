package com.hanyang.arttherapy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.ProfessorsRegisterRequestDto;
import com.hanyang.arttherapy.dto.response.ProfessorsResponseDto;
import com.hanyang.arttherapy.service.ProfessorsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/professors")
@RequiredArgsConstructor
public class ProfessorsController {

  private final ProfessorsService professorsService;

  // 교수진 전체 조회

  // 교수진 상세조회

  // 교수진 등록
  @PostMapping
  public ResponseEntity<ProfessorsResponseDto> registerProfessor(
      @RequestBody ProfessorsRegisterRequestDto requestDto, @RequestHeader("userId") Long userId) {
    professorsService.registerProfessor(requestDto, userId);
    return ResponseEntity.ok(new ProfessorsResponseDto("교수진 등록이 완료되었습니다."));
  }

  // 교수진 수정

  // 교수진 삭제
}
