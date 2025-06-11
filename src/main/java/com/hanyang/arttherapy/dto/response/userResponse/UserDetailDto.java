package com.hanyang.arttherapy.dto.response.userResponse;

import java.sql.Timestamp;

import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.domain.enums.UserStatus;

public record UserDetailDto(
    Long userNo,
    String userId,
    String email,
    String userName,
    String studentNo,
    Role role,
    UserStatus userStatus,
    Timestamp signupTimestamp,
    Timestamp signoutTimestamp,
    Timestamp bannedTimestamp,
    String cause) {}
