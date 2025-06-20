package com.hanyang.arttherapy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanyang.arttherapy.dto.response.ProfessorsResponseDto;
import com.hanyang.arttherapy.service.ProfessorViewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/professors")
@RequiredArgsConstructor
public class ProfessorViewController {

  private final ProfessorViewService professorViewService;

  // 교수진 정보 소개
  @GetMapping
  public ResponseEntity<List<ProfessorsResponseDto>> getAllProfessors() {
    return ResponseEntity.ok(professorViewService.getAllProfessors());
  }
}
