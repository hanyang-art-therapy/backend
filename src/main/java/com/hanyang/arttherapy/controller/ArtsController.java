package com.hanyang.arttherapy.controller;

import java.time.LocalDate;
import java.util.List;

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
  public ResponseEntity<List<ArtsListResponseDto>> getArtsByFilter(
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer cohort) {
    if (year == null) {
      year = LocalDate.now().getYear(); // 시스템의 현재 연도
    }

    List<ArtsListResponseDto> response;

    if (cohort != null) {
      response = artsService.getArtsByYearAndCohort(year, cohort);
    } else {
      response = artsService.getArtsByYear(year);
    }

    return ResponseEntity.ok(response);
  }

  // 작품명 검색
  @GetMapping("/arts/search")
  public ResponseEntity<List<ArtsListResponseDto>> searchArts(@RequestParam String keyword) {
    List<ArtsListResponseDto> response = artsService.searchArtsByName(keyword);
    return ResponseEntity.ok(response);
  }
}
