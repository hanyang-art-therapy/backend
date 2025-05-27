package com.hanyang.arttherapy.dto.response.noticeResponse;

public record CommonDataResponse<T>(String message, T data) {}
