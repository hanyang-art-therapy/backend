package com.hanyang.arttherapy.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.userRequest.UserRequestDto;
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
      @RequestParam(value = "userName", required = false) String userName, Long lastId) {
    return ResponseEntity.ok(adminUserService.getUsers(userName, lastId));
  }

  // 회원 상세 조회
  @GetMapping("/{userNo}")
  public ResponseEntity<UserDetailDto> getUserDetail(@PathVariable Long userNo) {
    return ResponseEntity.ok(adminUserService.getUserDetail(userNo));
  }

  // 회원 정보 수정
  @PatchMapping("/{userNo}")
  public ResponseEntity<CommonMessageResponse> updateUser(
      @PathVariable Long userNo, @RequestBody UserRequestDto requestDto) {
    String message = adminUserService.updateUser(userNo, requestDto);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }
}
