package com.hanyang.arttherapy.dto.response.userResponse;

import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.domain.enums.UserStatus;

public record UserDto(
    Long userNo, String userName, String studentNo, Role role, UserStatus userStatus) {}
