package com.hanyang.arttherapy.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.common.filter.CustomUserDetail;
import com.hanyang.arttherapy.dto.request.GalleriesRequestDto;
import com.hanyang.arttherapy.dto.response.GalleriesResponseDto;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.service.GalleriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/galleries")
public class GalleriesController {

  private final GalleriesService galleriesService;

  // 전시회 등록
  @PostMapping
  public ResponseEntity<CommonMessageResponse> create(
      @RequestBody @Valid GalleriesRequestDto dto,
      @AuthenticationPrincipal CustomUserDetail userDetail) {
    return ResponseEntity.status(201)
        .body(new CommonMessageResponse(galleriesService.save(dto, userDetail)));
  }

  // 전시회 전체 조회
  @GetMapping
  public ResponseEntity<List<GalleriesResponseDto>> getAllGalleries() {
    return ResponseEntity.ok(galleriesService.getAllGalleries());
  }

  // 전시회 상세조회
  @GetMapping("/{id}")
  public ResponseEntity<GalleriesResponseDto> getGalleryById(@PathVariable Long id) {
    return ResponseEntity.ok(galleriesService.getGalleryById(id));
  }

  // 전시회 수정
  @PatchMapping("/{id}")
  public ResponseEntity<CommonMessageResponse> update(
      @PathVariable Long id,
      @RequestBody @Valid GalleriesRequestDto dto,
      @AuthenticationPrincipal CustomUserDetail userDetail) {
    return ResponseEntity.ok(
        new CommonMessageResponse(galleriesService.update(id, dto, userDetail)));
  }

  // 전시회 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<CommonMessageResponse> delete(
      @PathVariable Long id, @AuthenticationPrincipal CustomUserDetail userDetail) {
    return ResponseEntity.ok(new CommonMessageResponse(galleriesService.delete(id, userDetail)));
  }
}
