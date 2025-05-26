package com.hanyang.arttherapy.dto.response.userResponse;

public record SigninResponse(
    Long userNo, String accessToken, com.hanyang.arttherapy.domain.enums.Role role) {}
