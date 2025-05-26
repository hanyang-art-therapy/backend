package com.hanyang.arttherapy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.*;
import com.hanyang.arttherapy.dto.response.artistResponse.ArtistResponseDto;
import com.hanyang.arttherapy.dto.response.artistResponse.ArtistResponseListDto;
import com.hanyang.arttherapy.dto.response.artistResponse.ArtistUpdateResponse;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.service.ArtistsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/admin/artists")
@RequiredArgsConstructor
public class ArtistController {

  private final ArtistsService artistsService;

  @PostMapping
  public ResponseEntity<CommonMessageResponse> registerArtist(@RequestBody ArtistRequestDto dto) {
    String message = artistsService.registerArtist(dto);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  @GetMapping("{artistsNo}")
  public ResponseEntity<ArtistResponseDto> getArtist(@PathVariable Long artistsNo) {
    return ResponseEntity.ok(artistsService.getArtist(artistsNo));
  }

  @GetMapping
  public ResponseEntity<ArtistResponseListDto> getArtists() {
    return ResponseEntity.ok(artistsService.getArtists());
  }

  @PatchMapping("/{artistsNo}")
  public ResponseEntity<ArtistUpdateResponse<ArtistResponseDto>> updateArtist(
      @PathVariable Long artistsNo, @RequestBody ArtistRequestDto dto) {
    ArtistUpdateResponse<ArtistResponseDto> response = artistsService.updateArtist(artistsNo, dto);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{artistsNo}")
  public ResponseEntity<CommonMessageResponse> deleteArtist(@PathVariable Long artistsNo) {
    String message = artistsService.deleteArtist(artistsNo);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }
}
