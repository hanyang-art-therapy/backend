package com.hanyang.arttherapy.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.dto.request.PasswordResetRequest;
import com.hanyang.arttherapy.dto.request.TemporaryPasswordRequest;
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
  public ResponseEntity<?> findUserId(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    String userName = request.get("userName");
    Users user = userService.findByEmailAndUserName(email, userName);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 정보와 일치하는 사용자가 없습니다.");
    }

    // 아이디 마스킹 처리 (앞 4자리만 보이고 나머지는 *)
    String originalId = user.getUserId();
    String maskedId = maskUserId(originalId);

    return ResponseEntity.ok(Map.of("userId", maskedId));
  }

  private String maskUserId(String originalId) {
    if (originalId.length() <= 4) {
      return originalId;
    }
    return originalId.substring(0, 4) + "*".repeat(originalId.length() - 4);
  }

  @PostMapping("/find-password")
  public ResponseEntity<String> newPassword(@RequestBody TemporaryPasswordRequest request) {
    String message = userService.newPassword(request.userId(), request.email());
    return ResponseEntity.ok(message);
  }

  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
    userService.resetPassword(request);
    return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
  }
}
