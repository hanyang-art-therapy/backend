package com.hanyang.arttherapy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.service.GalleriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/galleries")
public class GalleriesController {

  private final GalleriesService galleriesService;

  //  전시회 전체 조회
  @GetMapping
  public ResponseEntity<List<Galleries>> getAllGalleries() {
    List<Galleries> galleries = galleriesService.getAllGalleries();
    return ResponseEntity.ok(galleries);
  }

  //  전시회 개별 조회
  @GetMapping("/{id}")
  public ResponseEntity<Galleries> getGalleryById(@PathVariable Long id) {
    Galleries gallery = galleriesService.getGalleryById(id);
    return ResponseEntity.ok(gallery);
  }
}
