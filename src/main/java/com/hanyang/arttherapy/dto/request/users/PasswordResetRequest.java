package com.hanyang.arttherapy.dto.request.users;

public record PasswordResetRequest(String userId, String currentPassword, String newPassword) {}
