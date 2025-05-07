package com.hanyang.arttherapy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.service.GalleriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/galleries")
public class GalleriesController {

  private final GalleriesService galleriesService;

  //  전시회 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    galleriesService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
