package com.hanyang.arttherapy.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.common.util.JwtUtil;
import com.hanyang.arttherapy.dto.request.users.*;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.dto.response.userResponse.SigninResponse;
import com.hanyang.arttherapy.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final JwtUtil jwtUtil;

  @GetMapping("/check-id")
  public ResponseEntity<Boolean> checkId(@RequestParam String userId) {
    return ResponseEntity.ok(!userService.existsByUserId(userId));
  }

  @GetMapping("/check-studentNo")
  public ResponseEntity<Boolean> checkStudentNo(@RequestParam String studentNo) {
    return ResponseEntity.ok(!userService.existsByStudentNo(studentNo));
  }

  @PostMapping("/check-email")
  public ResponseEntity<CommonMessageResponse> checkEmail(@RequestBody EmailRequest request) {
    String message = userService.checkEmail(request);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  @PostMapping("/check-code")
  public ResponseEntity<CommonMessageResponse> verifyEmail(
      @RequestBody VerificationRequest request) {
    String message = userService.verifyEmailCode(request);
    return ResponseEntity.ok(new CommonMessageResponse(message));
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
    userService.signup(request);
    return ResponseEntity.ok().build();
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
      HttpServletRequest httpRequest, HttpServletResponse httpresponse) {
    String ip = getClientIp(httpRequest);
    String userAgent = httpRequest.getHeader("User-Agent");
    String refreshToken = jwtUtil.refreshTokenFromCookie(httpRequest);
    SigninResponse refresh = userService.newAccessToken(ip, userAgent, refreshToken, httpresponse);
    return ResponseEntity.ok(refresh);
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
    ResponseCookie deleteCookie = jwtUtil.deleteRefreshTokenCookie();
    // JSON 응답 + Set-Cookie 헤더 함께 반환
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .body(new CommonMessageResponse(message));
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty()) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
