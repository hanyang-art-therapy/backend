package com.hanyang.arttherapy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  //    private final JwtUtil jwtUtil;

  @GetMapping("/check-id")
  public ResponseEntity<Boolean> checkId(@RequestParam String userId) {
    return ResponseEntity.ok(!userService.existsByUserId(userId));
  }

  @GetMapping("/check-email")
  public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
    return ResponseEntity.ok(!userService.existsByEmail(email));
  }

  @GetMapping("/check-studentNo")
  public ResponseEntity<Boolean> checkStudentNo(@RequestParam String studentNo) {
    return ResponseEntity.ok(!userService.existsByStudentNo(studentNo));
  }
}
