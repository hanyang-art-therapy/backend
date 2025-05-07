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

  //  전시회 등록
  @PostMapping
  public ResponseEntity<Galleries> create(@RequestBody Galleries galleries) {
    Galleries saved = galleriesService.save(galleries);
    return ResponseEntity.status(201).body(saved);
  }
}
