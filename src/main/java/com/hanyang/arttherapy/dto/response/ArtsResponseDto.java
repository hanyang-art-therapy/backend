package com.hanyang.arttherapy.dto.response;

import java.util.List;

public record ArtsResponseDto(
    Long artsNo,
    String artName,
    String caption,
    String description,
    String createdAt,
    GalleryResponseDto galleries,
    List<ArtistResponseDto> artists,
    List<FileResponseDto> files,
    List<ReviewResponseDto> reviews) {}
