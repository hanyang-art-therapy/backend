package com.hanyang.arttherapy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.common.filter.CustomUserDetail;
import com.hanyang.arttherapy.dto.request.admin.AdminArtsPatchRequestDto;
import com.hanyang.arttherapy.dto.request.admin.AdminArtsRequestDto;
import com.hanyang.arttherapy.dto.response.AdminArtsDetailResponseDto;
import com.hanyang.arttherapy.dto.response.AdminArtsListResponseDto;
import com.hanyang.arttherapy.dto.response.CommonScrollResponse;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.service.AdminArtsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/arts")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminArtsController {

  private final AdminArtsService adminArtsService;

  // 작품 등록
  @PostMapping
  public ResponseEntity<CommonMessageResponse> register(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @RequestBody AdminArtsRequestDto request) {
    String message = adminArtsService.register(request, userDetail);
    return ResponseEntity.status(201).body(new CommonMessageResponse(message));
  }

  // 작품 수정
  @PatchMapping("/{artsNo}")
  public ResponseEntity<CommonMessageResponse> update(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PathVariable Long artsNo,
      @RequestBody AdminArtsPatchRequestDto request) {
    String message = adminArtsService.update(artsNo, request, userDetail);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  // 작품 삭제
  @DeleteMapping("/{artsNo}")
  public ResponseEntity<CommonMessageResponse> delete(
      @AuthenticationPrincipal CustomUserDetail userDetail, @PathVariable Long artsNo) {
    String message = adminArtsService.delete(artsNo, userDetail);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  // 무한 스크롤 기반 전체 조회 or 검색 조회
  @GetMapping
  public ResponseEntity<CommonScrollResponse<AdminArtsListResponseDto>> getArtsWithScroll(
      @RequestParam(required = false) String filter,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Long lastId,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(adminArtsService.getArtsWithScroll(filter, keyword, lastId, size));
  }

  // 작품 상세 조회
  @GetMapping("/{artsNo}")
  public ResponseEntity<AdminArtsDetailResponseDto> getArtDetail(@PathVariable Long artsNo) {
    return ResponseEntity.ok(adminArtsService.getArtDetail(artsNo));
  }
}
