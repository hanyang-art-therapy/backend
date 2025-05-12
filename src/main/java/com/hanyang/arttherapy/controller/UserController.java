package com.hanyang.arttherapy.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.common.util.JwtUtil;
import com.hanyang.arttherapy.dto.request.userRequest.*;
import com.hanyang.arttherapy.dto.response.userResponse.*;
import com.hanyang.arttherapy.service.UserService;

import lombok.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final JwtUtil jwtUtil;

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

  @PostMapping("/sign-up")
  public ResponseEntity<CommonMessageResponse> sigup(@RequestBody SignupRequest request) {
    String message = userService.signup(request);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  @PostMapping("/sign-in")
  public ResponseEntity<?> signin(
      @RequestBody SigninRequest request,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {

    // 클라이언트의 IP와 User-Agent를 받기
    String ip = getClientIp(httpRequest);
    String userAgent = httpRequest.getHeader("User-Agent");

    SigninResponse response = userService.signin(request, ip, userAgent, httpResponse);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> newAccessToken(
      HttpServletRequest httpRequest, HttpServletResponse response) {
    String ip = getClientIp(httpRequest);
    String userAgent = httpRequest.getHeader("User-Agent");
    TokenResponse token = userService.newAccessToken(ip, userAgent);
    return ResponseEntity.ok(token);
  }

  @PostMapping("/sign-out")
  public ResponseEntity<CommonMessageResponse> logout(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    String ip = getClientIp(httpRequest);
    String userAgent = httpRequest.getHeader("User-Agent");
    String refreshToken = jwtUtil.refreshTokenFromCookie(httpRequest);
    // 로그아웃 처리
    String message = userService.logout(ip, userAgent, refreshToken);
    // 쿠키에서 refreshToken 삭제
    jwtUtil.deleteRefreshTokenCookie(httpResponse);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty()) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
