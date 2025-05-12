package com.hanyang.arttherapy.dto.response;

public record ArtsListResponseDto(
    Long artsNo,
    String artName,
    int year,
    String thumbnailUrl,
    String artistInfo,
    GalleryResponseDto galleryResponse) {}
