package com.hanyang.arttherapy.dto.response.userResponse;

import com.hanyang.arttherapy.domain.Users;

public record UserResponse(
    String userId, String email, String userName, String studentNo, String token) {
  public static UserResponse from(Users user, String token) {
    return new UserResponse(
        user.getUserId(), user.getEmail(), user.getUserName(), user.getStudentNo(), token);
  }
}
