package com.hanyang.arttherapy.dto.response.artistResponse;

import java.util.List;

public record ArtistScrollResponseDto(
    List<ArtistResponseDto> artists, Long lastNo, boolean hasNext) {}
