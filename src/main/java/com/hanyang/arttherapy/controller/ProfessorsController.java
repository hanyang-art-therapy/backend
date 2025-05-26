package com.hanyang.arttherapy.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.ProfessorsRequestDto;
import com.hanyang.arttherapy.dto.response.ProfessorsResponseDto;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.service.ProfessorsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/professors")
@RequiredArgsConstructor
public class ProfessorsController {

  private final ProfessorsService professorsService;

  // 교수진 전체 조회
  @GetMapping
  public ResponseEntity<List<ProfessorsResponseDto>> getAllProfessors() {
    return ResponseEntity.ok(professorsService.getAllProfessors());
  }

  // 교수진 상세조회
  @GetMapping("/{professorNo}")
  public ResponseEntity<ProfessorsResponseDto> getProfessorDetail(@PathVariable Long professorNo) {
    return ResponseEntity.ok(professorsService.getProfessorDetail(professorNo));
  }

  // 교수진 등록
  @PostMapping
  public ResponseEntity<CommonMessageResponse> createProfessor(
      @RequestBody @Valid ProfessorsRequestDto requestDto) {
    String message = professorsService.saveProfessor(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(new CommonMessageResponse(message));
  }

  // 교수진 수정
  @PatchMapping("/{professorNo}")
  public ResponseEntity<CommonMessageResponse> updateProfessor(
      @PathVariable Long professorNo, @RequestBody @Valid ProfessorsRequestDto requestDto) {
    String message = professorsService.updateProfessor(professorNo, requestDto);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  // 교수진 삭제
  @DeleteMapping("/{professorNo}")
  public ResponseEntity<CommonMessageResponse> deleteProfessor(@PathVariable Long professorNo) {
    String message = professorsService.deleteProfessor(professorNo);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }
}
