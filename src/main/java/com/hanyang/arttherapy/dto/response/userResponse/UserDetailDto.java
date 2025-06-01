package com.hanyang.arttherapy.dto.response.userResponse;

import java.sql.Timestamp;

import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.domain.enums.UserStatus;

public record UserDetailDto(
    Long userNo,
    String userId,
    String passWord,
    String email,
    String userName,
    String studentNo,
    Role role,
    UserStatus userStatus,
    Timestamp signinTimestamp,
    Timestamp signoutTimestamp,
    Timestamp bannedTimestamp,
    String cause) {}
