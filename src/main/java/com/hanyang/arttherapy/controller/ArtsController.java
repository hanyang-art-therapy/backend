// package com.hanyang.arttherapy.controller;
//
// import java.util.LinkedHashMap;
// import java.util.Map;
//
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
//
// import com.hanyang.arttherapy.dto.response.ArtsListResponseDto;
// import com.hanyang.arttherapy.dto.response.ArtsResponseDto;
// import com.hanyang.arttherapy.service.ArtsService;
//
// import lombok.RequiredArgsConstructor;
//
// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/galleries")
// public class ArtsController {
//
//  private final ArtsService artsService;
//
//  // 기본 작품 조회 - galleriesNo를 기준으로 시스템 연도 조회
//  @GetMapping("/{galleriesNo}/arts")
//  public ResponseEntity<Map<String, Object>> getArtsByGallery(
//      @PathVariable Long galleriesNo, Pageable pageable) {
//
//    Page<ArtsListResponseDto> response = artsService.getArtsByYear(galleriesNo, pageable);
//
//    // 응답 포맷 통일
//    Map<String, Object> result = createResponseMap(response);
//
//    return ResponseEntity.ok(result);
//  }
//
//  // 드롭다운 조회 - 연도별, 기수별, 혹은 둘 다 필터링
//  @GetMapping("/arts")
//  public ResponseEntity<Map<String, Object>> getArtsByFilter(
//      @RequestParam(required = false) Integer year,
//      @RequestParam(required = false) Integer cohort,
//      Pageable pageable) {
//
//    Page<ArtsListResponseDto> response;
//
//    // 연도 + 기수 조회
//    if (year != null && cohort != null) {
//      response = artsService.getArtsByYearAndCohort(year, cohort, pageable);
//    }
//    // 연도만 조회
//    else if (year != null) {
//      response = artsService.getArtsBySelectedYear(year, pageable);
//    }
//    // 기수만 조회
//    else if (cohort != null) {
//      response = artsService.getArtsByCohort(cohort, pageable);
//    } else {
//      return ResponseEntity.badRequest().body(Map.of("message", "연도 또는 기수를 선택해야 합니다."));
//    }
//
//    // 응답 포맷 통일
//    Map<String, Object> result = createResponseMap(response);
//
//    return ResponseEntity.ok(result);
//  }
//
//  // 응답 포맷 통일
//  private Map<String, Object> createResponseMap(Page<ArtsListResponseDto> response) {
//    Map<String, Object> result = new LinkedHashMap<>();
//    result.put("content", response.getContent());
//    result.put("page", response.getNumber());
//    result.put("size", response.getSize());
//    result.put("totalElements", response.getTotalElements());
//    result.put("totalPages", response.getTotalPages());
//    result.put("last", response.isLast());
//
//    if (response.isEmpty()) {
//      result.put("message", "조회된 작품이 없습니다.");
//    }
//
//    return result;
//  }
//
//  // 작품 상세 조회
//  @GetMapping("/{galleriesNo}/arts/{artsNo}")
//  public ResponseEntity<ArtsResponseDto> getArtDetail(
//      @PathVariable Long galleriesNo, @PathVariable Long artsNo) {
//    ArtsResponseDto response = artsService.getArtDetail(galleriesNo, artsNo);
//    return ResponseEntity.ok(response);
//  }
// }
