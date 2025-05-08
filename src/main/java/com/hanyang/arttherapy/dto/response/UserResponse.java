package com.hanyang.arttherapy.dto.response;

import com.hanyang.arttherapy.domain.Users;

public record UserResponse(String userId, String email, String studentNo, String token) {
  public static UserResponse from(Users user, String token) {
    return new UserResponse(user.getUserId(), user.getEmail(), user.getStudentNo(), token);
  }
}
