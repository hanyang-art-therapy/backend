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

  @GetMapping("{artistNo}")
  public ResponseEntity<ArtistResponseDto> getArtist(@PathVariable Long artistNo) {
    return ResponseEntity.ok(artistsService.getArtist(artistNo));
  }

  @PatchMapping("/{artistNo}")
  public ResponseEntity<CommonMessageResponse> updateArtist(
      @PathVariable Long artistNo, @RequestBody ArtistRequestDto dto) {
    String message = artistsService.updateArtist(artistNo, dto);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }

  @DeleteMapping("/{artistNo}")
  public ResponseEntity<CommonMessageResponse> deleteArtist(@PathVariable Long artistNo) {
    String message = artistsService.deleteArtist(artistNo);
    return ResponseEntity.ok(new CommonMessageResponse(message));
  }
}
