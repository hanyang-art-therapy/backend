package com.hanyang.arttherapy.dto.request;

public record PasswordResetRequest(String userId, String currentPassword, String newPassword) {}
