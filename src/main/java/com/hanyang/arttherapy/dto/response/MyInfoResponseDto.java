package com.hanyang.arttherapy.dto.response;

import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.enums.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyInfoResponseDto {
  private final String userId;
  private final String email;
  private final String userName;
  private final Role role;
  private final String studentNo;

  public static MyInfoResponseDto from(Users user, String userId) {
    return MyInfoResponseDto.builder()
        .userId(userId)
        .email(user.getEmail())
        .userName(user.getUserName())
        .role(user.getRole())
        .studentNo(user.getStudentNo())
        .build();
  }

  public static MyInfoResponseDto from(Users user) {
    return from(user, null); // 또는 기본 userId로 user.getEmail() 넣어도 됨
  }
}
