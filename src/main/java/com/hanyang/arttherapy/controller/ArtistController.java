package com.hanyang.arttherapy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.ArtistRequestDto;
import com.hanyang.arttherapy.dto.response.artistResponse.ArtistResponseDto;
import com.hanyang.arttherapy.dto.response.artistResponse.ArtistScrollResponseDto;
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

  @GetMapping
  public ResponseEntity<ArtistScrollResponseDto> getArtists(
      @RequestParam(required = false) String filter,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Long lastId,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(artistsService.searchArtists(filter, keyword, lastId, size));
  }

  @GetMapping("{artistsNo}")
  public ResponseEntity<ArtistResponseDto> getArtist(@PathVariable Long artistsNo) {
    return ResponseEntity.ok(artistsService.getArtist(artistsNo));
  }

  @PatchMapping("/{artistsNo}")
  public ResponseEntity<CommonMessageResponse> updateArtist(
      @PathVariable Long artistsNo, @RequestBody ArtistRequestDto dto) {
    String message = artistsService.updateArtist(artistsNo, dto);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  @DeleteMapping("/{artistsNo}")
  public ResponseEntity<CommonMessageResponse> deleteArtist(@PathVariable Long artistsNo) {
    String message = artistsService.deleteArtist(artistsNo);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }
}
