package com.hanyang.arttherapy.dto.response;

import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.enums.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyInfoResponseDto {
  private final Long userId;
  private final String email;
  private final String userName;
  private final Role role;
  private final String studentNo;

  public static MyInfoResponseDto from(Users user) {
    return MyInfoResponseDto.builder()
        .userId(user.getUserNo())
        .email(user.getEmail())
        .userName(user.getUserName())
        .role(user.getRole())
        .studentNo(user.getStudentNo())
        .build();
  }
}
