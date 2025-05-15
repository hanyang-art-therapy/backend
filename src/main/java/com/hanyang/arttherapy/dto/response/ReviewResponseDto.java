package com.hanyang.arttherapy.dto.response;

import java.util.List;

public record ReviewResponseDto(
    Long reviewNo, String reviewText, String userName, Long userNo, List<FileResponseDto> files) {}
