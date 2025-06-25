package com.hanyang.arttherapy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanyang.arttherapy.dto.response.GalleriesResponseDto;
import com.hanyang.arttherapy.service.GalleriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/galleries")
@RequiredArgsConstructor
public class GalleriesPublicController {

  private final GalleriesService galleriesService;

  @GetMapping("/intro/{year}")
  public ResponseEntity<GalleriesResponseDto> getGalleryIntro(@PathVariable int year) {
    return ResponseEntity.ok(galleriesService.getGalleryIntro(year));
  }
}
