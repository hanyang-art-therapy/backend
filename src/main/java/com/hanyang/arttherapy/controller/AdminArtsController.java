package com.hanyang.arttherapy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.AdminArtsPatchRequestDto;
import com.hanyang.arttherapy.dto.request.AdminArtsRequestDto;
import com.hanyang.arttherapy.dto.response.AdminArtsDetailResponseDto;
import com.hanyang.arttherapy.dto.response.AdminArtsListResponseDto;
import com.hanyang.arttherapy.dto.response.CommonScrollResponse;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.service.AdminArtsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/arts")
@RequiredArgsConstructor
public class AdminArtsController {

  private final AdminArtsService adminArtsService;

  //  작품 등록
  @PostMapping
  public ResponseEntity<CommonMessageResponse> register(@RequestBody AdminArtsRequestDto request) {
    String message = adminArtsService.register(request);
    return ResponseEntity.status(201).body(new CommonMessageResponse(message));
  }

  // 작품 수정
  @PatchMapping("/{artsNo}")
  public ResponseEntity<CommonMessageResponse> update(
      @PathVariable Long artsNo, @RequestBody AdminArtsPatchRequestDto request) {
    String message = adminArtsService.update(artsNo, request);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  // 작품 삭제
  @DeleteMapping("/{artsNo}")
  public ResponseEntity<CommonMessageResponse> delete(@PathVariable Long artsNo) {
    String message = adminArtsService.delete(artsNo);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  // 작품 전체 조회 & 검색 (keyword가 있으면 검색, 없으면 전체 조회)
  @GetMapping
  public ResponseEntity<List<AdminArtsListResponseDto>> getAllOrSearchArts(
      @RequestParam(required = false) String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return ResponseEntity.ok(adminArtsService.getAllArts());
    }
    return ResponseEntity.ok(adminArtsService.searchArts(keyword));
  }

  // 5. 작품 상세 조회
  @GetMapping("/{artsNo}")
  public ResponseEntity<AdminArtsDetailResponseDto> getArtDetail(@PathVariable Long artsNo) {
    return ResponseEntity.ok(adminArtsService.getArtDetail(artsNo));
  }

  // 무한 스크롤 조회 (전체 + 검색)
  @GetMapping("/scroll")
  public ResponseEntity<CommonScrollResponse<AdminArtsListResponseDto>> getScrollArts(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Long lastId,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(adminArtsService.searchArtsWithScroll(keyword, lastId, size));
  }
}
