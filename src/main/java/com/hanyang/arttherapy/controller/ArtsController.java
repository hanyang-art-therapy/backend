// package com.hanyang.arttherapy.controller;
//
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
//
// import com.hanyang.arttherapy.dto.response.ArtsDetailResponseDto;
// import com.hanyang.arttherapy.service.ArtsService;
//
// @RestController
// @RequestMapping("/api/galleries/{galleriesNo}/arts")
// public class ArtsController {
//
//  private final ArtsService artsService;
//
//  public ArtsController(ArtsService artsService) {
//    this.artsService = artsService;
//  }
//
//  @GetMapping("/{artsNo}")
//  public ResponseEntity<ArtsDetailResponseDto> getArtDetail(
//      @PathVariable Long galleriesNo, @PathVariable Long artsNo) {
//    ArtsDetailResponseDto response = artsService.getArtDetail(galleriesNo, artsNo);
//    return ResponseEntity.ok(response);
//  }
// }
