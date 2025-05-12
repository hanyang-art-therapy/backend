package com.hanyang.arttherapy.dto.response;

import java.util.List;

public record ArtsResponseDto(
    Long artsNo,
    String artName,
    String caption,
    String description,
    String formattedDate,
    GalleryResponseDto galleryResponse,
    List<ArtistResponseDto> artists,
    List<FileResponseDto> files,
    List<ReviewResponseDto> reviews) {}
