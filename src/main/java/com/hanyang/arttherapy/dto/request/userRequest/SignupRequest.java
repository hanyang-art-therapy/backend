package com.hanyang.arttherapy.dto.request.userRequest;

public record SignupRequest(
    String userId, String password, String email, String userName, String studentNo) {}
