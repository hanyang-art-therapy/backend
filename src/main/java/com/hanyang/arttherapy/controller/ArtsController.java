package com.hanyang.arttherapy.controller;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.response.ArtsListResponseDto;
import com.hanyang.arttherapy.dto.response.ArtsResponseDto;
import com.hanyang.arttherapy.service.ArtsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/galleries")
public class ArtsController {

  private final ArtsService artsService;

  // 작품 상세 조회
  @GetMapping("/{galleriesNo}/arts/{artsNo}")
  public ResponseEntity<ArtsResponseDto> getArtDetail(
      @PathVariable Long galleriesNo, @PathVariable Long artsNo) {
    ArtsResponseDto response = artsService.getArtDetail(galleriesNo, artsNo);
    return ResponseEntity.ok(response);
  }

  // 작품 전체 조회 및 연도별, 기수별, 혹은 둘 다 필터링
  @GetMapping("/arts")
  public ResponseEntity<?> getArtsByFilter(
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer cohort,
      Pageable pageable) {

    Page<ArtsListResponseDto> response;

    // 둘 다 없을 때: 시스템 연도로 조회
    if (year == null && cohort == null) {
      year = LocalDate.now().getYear();
      response = artsService.getArtsByYear(year, pageable);
    }
    // 연도 + 기수 조회
    else if (year != null && cohort != null) {
      response = artsService.getArtsByYearAndCohort(year, cohort, pageable);
    }
    // 연도만 조회
    else if (year != null) {
      response = artsService.getArtsByYear(year, pageable);
    }
    // 기수만 조회
    else {
      response = artsService.getArtsByCohort(cohort, pageable);
    }

    // 응답 포맷 통일
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("content", response.getContent());
    result.put("page", response.getNumber());
    result.put("size", response.getSize());
    result.put("totalElements", response.getTotalElements());
    result.put("totalPages", response.getTotalPages());
    result.put("last", response.isLast());

    return ResponseEntity.ok(result);
  }
}
