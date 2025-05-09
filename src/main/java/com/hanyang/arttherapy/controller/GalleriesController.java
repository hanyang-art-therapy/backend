package com.hanyang.arttherapy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.service.GalleriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/galleries")
public class GalleriesController {

  private final GalleriesService galleriesService;

  // 전시회 등록
  @PostMapping
  // @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> create(@RequestBody Galleries galleries) {
    try {
      Galleries saved = galleriesService.save(galleries);
      return ResponseEntity.status(201).body(saved);
    } catch (Exception e) {
      e.printStackTrace(); // 로그 출력
      return ResponseEntity.status(500).body(Map.of("message", "서버 오류로 등록이 실패했습니다."));
    }
  }

  // 전시회 전체 조회
  @GetMapping
  public ResponseEntity<List<Galleries>> getAllGalleries() {
    List<Galleries> galleries = galleriesService.getAllGalleries();
    return ResponseEntity.ok(galleries);
  }

  // 전시회 개별 조회
  @GetMapping("/{id}")
  public ResponseEntity<Galleries> getGalleryById(@PathVariable Long id) {
    Galleries gallery = galleriesService.getGalleryById(id);
    return ResponseEntity.ok(gallery);
  }

  // 전시회 수정
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Galleries> update(@PathVariable Long id, @RequestBody Galleries updated) {
    Galleries updatedGallery = galleriesService.update(id, updated);
    return ResponseEntity.ok(updatedGallery);
  }

  // 전시회 삭제
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    galleriesService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
