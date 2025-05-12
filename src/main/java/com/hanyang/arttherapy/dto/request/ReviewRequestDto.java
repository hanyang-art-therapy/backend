package com.hanyang.arttherapy.dto.request;

import java.util.List;

public record ReviewRequestDto(String reviewText, List<Long> filesNo) {}
