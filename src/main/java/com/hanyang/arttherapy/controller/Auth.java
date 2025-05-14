package com.hanyang.arttherapy.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanyang.arttherapy.domain.Users;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class Auth {

  @GetMapping("/test")
  public String test(@AuthenticationPrincipal Users user) {
    if (user == null) {
      return "인증되지 않은 사용자입니다.";
    }
    return "인증된 사용자: " + user.getUserName() + " (ID: " + user.getUserId() + ")";
  }
}
