package com.hanyang.arttherapy.dto.request.users;

public record SignupRequest(
    String userId, String password, String email, String userName, String studentNo) {}
