package com.hanyang.arttherapy.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.GalleriesRequestDto;
import com.hanyang.arttherapy.dto.response.GalleriesResponseDto;
import com.hanyang.arttherapy.service.GalleriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/galleries")
public class GalleriesController {

  private final GalleriesService galleriesService;

  @PostMapping
  public ResponseEntity<GalleriesResponseDto> create(
      @RequestBody @Valid GalleriesRequestDto dto, @RequestHeader("userId") Long userId) {
    return ResponseEntity.status(201).body(galleriesService.save(dto, userId));
  }

  @GetMapping
  public ResponseEntity<List<GalleriesResponseDto>> getAllGalleries() {
    return ResponseEntity.ok(galleriesService.getAllGalleries());
  }

  @GetMapping("/{id}")
  public ResponseEntity<GalleriesResponseDto> getGalleryById(@PathVariable Long id) {
    return ResponseEntity.ok(galleriesService.getGalleryById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<GalleriesResponseDto> update(
      @PathVariable Long id, @RequestBody @Valid GalleriesRequestDto dto) {
    return ResponseEntity.ok(galleriesService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    galleriesService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
