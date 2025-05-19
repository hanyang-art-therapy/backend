package com.hanyang.arttherapy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.AdminArtsRequestDto;
import com.hanyang.arttherapy.dto.response.AdminArtsDetailResponseDto;
import com.hanyang.arttherapy.dto.response.AdminArtsListResponseDto;
import com.hanyang.arttherapy.service.AdminArtsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/arts")
@RequiredArgsConstructor
public class AdminArtsController {

  private final AdminArtsService adminArtsService;

  // 1. 작품 등록
  @PostMapping
  public ResponseEntity<Void> register(@RequestBody AdminArtsRequestDto request) {
    adminArtsService.register(request);
    return ResponseEntity.status(201).build();
  }

  // 2. 작품 수정
  @PutMapping("/{artsNo}")
  public ResponseEntity<Void> update(
      @PathVariable Long artsNo, @RequestBody AdminArtsRequestDto request) {
    adminArtsService.update(artsNo, request);
    return ResponseEntity.ok().build();
  }

  // 3. 작품 삭제
  @DeleteMapping("/{artsNo}")
  public ResponseEntity<Void> delete(@PathVariable Long artsNo) {
    adminArtsService.delete(artsNo);
    return ResponseEntity.noContent().build();
  }

  // 4. 작품 전체 조회
  @GetMapping
  public ResponseEntity<List<AdminArtsListResponseDto>> getAllArts() {
    return ResponseEntity.ok(adminArtsService.getAllArts());
  }

  // 5. 작품 상세 조회
  @GetMapping("/{artsNo}")
  public ResponseEntity<AdminArtsDetailResponseDto> getArtDetail(@PathVariable Long artsNo) {
    return ResponseEntity.ok(adminArtsService.getArtDetail(artsNo));
  }
}
