package com.hanyang.arttherapy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.service.GalleriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/galleries")
public class GalleriesController {

  private final GalleriesService galleriesService;

  // 전시회 등록
  @PostMapping
  public ResponseEntity<?> create(@RequestBody Galleries galleries) {
    try {
      Galleries saved = galleriesService.save(galleries);
      return ResponseEntity.status(201).body(saved);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("message", "서버 오류로 등록이 실패했습니다."));
    }
  }

  // 전시회 전체 조회
  @GetMapping
  public ResponseEntity<List<Galleries>> getAllGalleries() {
    return ResponseEntity.ok(galleriesService.getAllGalleries());
  }

  // 전시회 개별 조회
  @GetMapping("/{galleriesNo}")
  public ResponseEntity<Galleries> getGalleryById(@PathVariable Long galleriesNo) {
    return ResponseEntity.ok(galleriesService.getGalleryById(galleriesNo));
  }

  // 전시회 수정
  @PutMapping("/{galleriesNo}")
  public ResponseEntity<Galleries> update(
      @PathVariable Long galleriesNo, @RequestBody Galleries updated) {
    Galleries updatedGallery = galleriesService.update(galleriesNo, updated);
    return ResponseEntity.ok(updatedGallery);
  }

  // 전시회 삭제
  @DeleteMapping("/{galleriesNo}")
  public ResponseEntity<Void> delete(@PathVariable Long galleriesNo) {
    galleriesService.delete(galleriesNo);
    return ResponseEntity.noContent().build();
  }
}
