package com.hanyang.arttherapy.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanyang.arttherapy.common.util.JwtUtil;
import com.hanyang.arttherapy.dto.response.userResponse.SigninResponse;
import com.hanyang.arttherapy.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final JwtUtil jwtUtil;
  private final UserService userService;

  @PostMapping("/refresh")
  public ResponseEntity<?> newAccessToken(
      HttpServletRequest httpRequest, HttpServletResponse httpresponse) {
    String ip = getClientIp(httpRequest);
    String userAgent = httpRequest.getHeader("User-Agent");
    String refreshToken = jwtUtil.refreshTokenFromCookie(httpRequest);
    SigninResponse refresh = userService.newAccessToken(ip, userAgent, refreshToken, httpresponse);
    return ResponseEntity.ok(refresh);
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty()) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
