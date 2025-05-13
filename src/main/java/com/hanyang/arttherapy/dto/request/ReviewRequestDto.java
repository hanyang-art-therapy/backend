package com.hanyang.arttherapy.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record ReviewRequestDto(String reviewText, List<Long> filesNo, List<MultipartFile> files) {}
