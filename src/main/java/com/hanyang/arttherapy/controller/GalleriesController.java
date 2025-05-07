package com.hanyang.arttherapy.controller;

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

  //  전시회 수정
  @PutMapping("/{id}")
  public ResponseEntity<Galleries> update(@PathVariable Long id, @RequestBody Galleries updated) {
    Galleries updatedGallery = galleriesService.update(id, updated);
    return ResponseEntity.ok(updatedGallery);
  }
}
