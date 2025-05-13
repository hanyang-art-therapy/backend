package com.hanyang.arttherapy.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.AdminArtsRequestDto;
import com.hanyang.arttherapy.dto.response.AdminArtsResponseDto;
import com.hanyang.arttherapy.service.AdminArtsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/arts")
public class AdminArtsController {

  private final AdminArtsService adminArtsService;

  @PostMapping
  public ResponseEntity<AdminArtsResponseDto> create(@RequestBody @Valid AdminArtsRequestDto dto) {
    return ResponseEntity.status(201).body(adminArtsService.save(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<AdminArtsResponseDto> update(
      @PathVariable Long id, @RequestBody @Valid AdminArtsRequestDto dto) {
    return ResponseEntity.ok(adminArtsService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    adminArtsService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<AdminArtsResponseDto>> getAllArts() {
    return ResponseEntity.ok(adminArtsService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<AdminArtsResponseDto> getArtById(@PathVariable Long id) {
    return ResponseEntity.ok(adminArtsService.findById(id));
  }
}
