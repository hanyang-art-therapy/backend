package com.hanyang.arttherapy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.userRequest.IdRequest;
import com.hanyang.arttherapy.dto.request.userRequest.PasswordResetRequest;
import com.hanyang.arttherapy.dto.request.userRequest.TemporaryPasswordRequest;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
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

  @PostMapping("/find-id")
  public ResponseEntity<CommonMessageResponse> findUserId(@RequestBody IdRequest request) {
    String message = userService.findByEmailAndUserName(request);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  @PostMapping("/find-password")
  public ResponseEntity<CommonMessageResponse> newPassword(
      @RequestBody TemporaryPasswordRequest request) {
    String message = userService.newPassword(request.userId(), request.email());
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<CommonMessageResponse> resetPassword(
      @RequestBody PasswordResetRequest request) {
    String message = userService.resetPassword(request);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }
}
