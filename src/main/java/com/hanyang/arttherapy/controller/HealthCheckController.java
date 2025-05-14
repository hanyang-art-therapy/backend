package com.hanyang.arttherapy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

  @GetMapping("/health-check")
  public ResponseEntity<Void> healthCheck() {
    return ResponseEntity.noContent().build();
  }
}
