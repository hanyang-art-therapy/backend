package com.hanyang.arttherapy.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.common.filter.CustomUserDetail;
import com.hanyang.arttherapy.dto.request.ProfessorsRequestDto;
import com.hanyang.arttherapy.dto.response.ProfessorsResponseDto;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.service.ProfessorsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/professors")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ProfessorsController {

  private final ProfessorsService professorsService;

  // 교수진 전체 조회 (공개)
  @GetMapping
  public ResponseEntity<List<ProfessorsResponseDto>> getAllProfessors() {
    return ResponseEntity.ok(professorsService.getAllProfessors());
  }

  // 교수진 상세조회 (공개)
  @GetMapping("/{professorNo}")
  public ResponseEntity<ProfessorsResponseDto> getProfessorDetail(@PathVariable Long professorNo) {
    return ResponseEntity.ok(professorsService.getProfessorDetail(professorNo));
  }

  // 교수진 등록 (관리자만)
  @PostMapping
  public ResponseEntity<CommonMessageResponse> createProfessor(
      @RequestBody @Valid ProfessorsRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetail userDetail) {
    String message = professorsService.saveProfessor(requestDto, userDetail);
    return ResponseEntity.status(HttpStatus.CREATED).body(new CommonMessageResponse(message));
  }

  // 교수진 수정 (관리자만)
  @PatchMapping("/{professorNo}")
  public ResponseEntity<CommonMessageResponse> updateProfessor(
      @PathVariable Long professorNo,
      @RequestBody @Valid ProfessorsRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetail userDetail) {
    String message = professorsService.updateProfessor(professorNo, requestDto, userDetail);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  // 교수진 삭제 (관리자만)
  @DeleteMapping("/{professorNo}")
  public ResponseEntity<CommonMessageResponse> deleteProfessor(
      @PathVariable Long professorNo, @AuthenticationPrincipal CustomUserDetail userDetail) {
    String message = professorsService.deleteProfessor(professorNo, userDetail);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }
}
