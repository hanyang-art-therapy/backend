package com.hanyang.arttherapy.dto.response.userResponse;

import com.hanyang.arttherapy.domain.Users;

public record UserDto(Long userNo, String userName, String userId, String studentNo) {
  public static UserDto fromEntity(Users user, Long index) {
    return new UserDto(user.getUserNo(), user.getUserName(), user.getUserId(), user.getStudentNo());
  }
}
