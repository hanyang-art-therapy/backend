package com.hanyang.arttherapy.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.common.filter.CustomUserDetail;
import com.hanyang.arttherapy.dto.request.users.UserRequestDto;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.dto.response.userResponse.UserDetailDto;
import com.hanyang.arttherapy.service.AdminUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

  private final AdminUserService adminUserService;

  // 전체 조회 또는 이름 검색 조회
  @GetMapping
  public ResponseEntity<Map<String, Object>> getUsers(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @RequestParam(value = "userName", required = false) String userName,
      @RequestParam(value = "lastId", required = false) Long lastId) {
    return ResponseEntity.ok(adminUserService.getUsers(userName, lastId, userDetail));
  }

  // 회원 상세 조회
  @GetMapping("/{userNo}")
  public ResponseEntity<UserDetailDto> getUserDetail(
      @AuthenticationPrincipal CustomUserDetail userDetail, @PathVariable Long userNo) {
    return ResponseEntity.ok(adminUserService.getUserDetail(userNo, userDetail));
  }

  // 회원 정보 수정
  @PatchMapping("/{userNo}")
  public ResponseEntity<CommonMessageResponse> updateUser(
      @AuthenticationPrincipal CustomUserDetail userDetail,
      @PathVariable Long userNo,
      @RequestBody UserRequestDto requestDto) {
    String message = adminUserService.updateUser(userNo, requestDto, userDetail);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }
}
