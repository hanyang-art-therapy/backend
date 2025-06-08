package com.hanyang.arttherapy.dto.request;

public record MypageUpdateRequest(
    String email, String verificationCode, String userName, String studentNo) {}
