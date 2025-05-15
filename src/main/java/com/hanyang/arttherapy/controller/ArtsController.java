package com.hanyang.arttherapy.controller;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.response.ArtsListResponseDto;
import com.hanyang.arttherapy.dto.response.ArtsResponseDto;
import com.hanyang.arttherapy.service.ArtsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/galleries")
public class ArtsController {

  private final ArtsService artsService;

  // 드롭다운 조회 - 연도별, 기수별, 혹은 둘 다 필터링
  @GetMapping("/arts")
  public ResponseEntity<Map<String, Object>> getArtsByFilter(
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer cohort,
      @RequestParam(required = false) Long lastId) {

    List<ArtsListResponseDto> response;

    // 최초 페이지 로딩 시 현재 연도로 조회
    if (year == null && cohort == null) {
      year = LocalDate.now().getYear();
      response = artsService.getArtsByYear(year, lastId);
    }
    // 연도 + 기수 조회
    else if (year != null && cohort != null) {
      response = artsService.getArtsByYearAndCohort(year, cohort, lastId);
    }
    // 연도만 조회
    else if (year != null) {
      response = artsService.getArtsBySelectedYear(year, lastId);
    }
    // 기수만 조회
    else {
      response = artsService.getArtsByCohort(cohort, lastId);
    }

    // 응답 포맷 통일
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("content", response);
    result.put("lastId", response.isEmpty() ? null : response.get(response.size() - 1).artsNo());

    return ResponseEntity.ok(result);
  }

  // 작품 상세 조회
  @GetMapping("/arts/{artsNo}")
  public ResponseEntity<ArtsResponseDto> getArtDetail(@PathVariable Long artsNo) {
    ArtsResponseDto response = artsService.getArtDetail(artsNo);
    return ResponseEntity.ok(response);
  }
}
