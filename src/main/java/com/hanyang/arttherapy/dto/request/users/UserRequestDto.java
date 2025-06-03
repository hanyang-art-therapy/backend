package com.hanyang.arttherapy.dto.request.users;

import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.domain.enums.UserStatus;

public record UserRequestDto(
    String email, String userName, String studentNo, Role role, UserStatus userStatus) {}
