package com.hanyang.arttherapy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.service.MyPageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/myPage")
@RequiredArgsConstructor
public class MyPageController {

  private final MyPageService myPageService;

  @PatchMapping("/withdraw")
  public ResponseEntity<CommonMessageResponse> withdraw(@RequestHeader("userId") Long userNo) {
    String message = myPageService.withdrawByUserNo(userNo);
    return ResponseEntity.ok(new CommonMessageResponse(message)); // 204 No Content
  }
}
