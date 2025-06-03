package com.hanyang.arttherapy.dto.request;

public record MypageEmailRequest(String email, String verificationCode, Long userNo) {}
