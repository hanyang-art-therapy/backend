package com.hanyang.arttherapy.dto.request;

public record MypageUpdateRequest(
    String email,
    String verificationCode,
    String userName, // ✅ 수정: name → userName
    String studentNo) {}
