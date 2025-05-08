package com.hanyang.arttherapy.dto.request.userRequest;

public record PasswordResetRequest(String userId, String currentPassword, String newPassword) {}
