package com.hanyang.arttherapy.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.response.ArtsResponseDto;
import com.hanyang.arttherapy.service.ArtsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/galleries")
public class ArtsController {

  private final ArtsService artsService;

  @GetMapping("/arts")
  public ResponseEntity<Map<String, Object>> getArtsByFilter(
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer cohort,
      @RequestParam(required = false) Long lastId) {

    Map<String, Object> result = artsService.getArtsByFilter(year, cohort, lastId);
    return ResponseEntity.ok(result);
  }

  // 작품 상세 조회
  @GetMapping("/arts/{artsNo}")
  public ResponseEntity<ArtsResponseDto> getArtDetail(@PathVariable Long artsNo) {
    ArtsResponseDto response = artsService.getArtDetail(artsNo);
    return ResponseEntity.ok(response);
  }
}
