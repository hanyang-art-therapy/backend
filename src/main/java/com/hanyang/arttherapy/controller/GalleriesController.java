package com.hanyang.arttherapy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.dto.request.GalleriesRequestDto;
import com.hanyang.arttherapy.dto.response.GalleriesResponseDto;
import com.hanyang.arttherapy.service.GalleriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/galleries")
public class GalleriesController {

  private final GalleriesService galleriesService;

  // 전시회 등록
  @PostMapping
  public ResponseEntity<?> create(@RequestBody GalleriesRequestDto requestDto) {
    try {
      Galleries saved = galleriesService.save(requestDto);
      return ResponseEntity.status(201).body(GalleriesResponseDto.from(saved));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("message", "서버 오류로 등록이 실패했습니다."));
    }
  }

  // 전시회 전체 조회
  @GetMapping
  public ResponseEntity<List<GalleriesResponseDto>> getAllGalleries() {
    List<GalleriesResponseDto> response =
        galleriesService.getAllGalleries().stream().map(GalleriesResponseDto::from).toList();
    return ResponseEntity.ok(response);
  }

  // 전시회 개별 조회
  @GetMapping("/{galleriesNo}")
  public ResponseEntity<GalleriesResponseDto> getGalleryById(@PathVariable Long galleriesNo) {
    Galleries gallery = galleriesService.getGalleryById(galleriesNo);
    return ResponseEntity.ok(GalleriesResponseDto.from(gallery));
  }

  // 전시회 수정
  @PutMapping("/{galleriesNo}")
  public ResponseEntity<GalleriesResponseDto> update(
      @PathVariable Long galleriesNo, @RequestBody GalleriesRequestDto requestDto) {
    Galleries updatedGallery = galleriesService.update(galleriesNo, requestDto);
    return ResponseEntity.ok(GalleriesResponseDto.from(updatedGallery));
  }

  // 전시회 삭제
  @DeleteMapping("/{galleriesNo}")
  public ResponseEntity<Void> delete(@PathVariable Long galleriesNo) {
    galleriesService.delete(galleriesNo);
    return ResponseEntity.noContent().build();
  }
}
