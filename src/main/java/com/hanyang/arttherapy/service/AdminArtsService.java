package com.hanyang.arttherapy.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.AdminArtsExceptionType;
import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.dto.request.AdminArtsRequestDto;
import com.hanyang.arttherapy.dto.response.AdminArtsDetailResponseDto;
import com.hanyang.arttherapy.dto.response.AdminArtsListResponseDto;
import com.hanyang.arttherapy.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminArtsService {

  private final ArtsRepository artsRepository;
  private final FilesRepository filesRepository;
  private final GalleriesRepository galleriesRepository;
  private final ArtArtistRelRepository artArtistRelRepository;
  private final ArtistsRepository artistsRepository;

  // 1. 작품 등록
  @Transactional
  public void register(AdminArtsRequestDto request) {
    Files file =
        filesRepository
            .findById(request.getFilesNo())
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.FILE_NOT_FOUND));
    Galleries gallery =
        galleriesRepository
            .findById(request.getGalleriesNo())
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.GALLERY_NOT_FOUND));

    Arts art =
        Arts.builder()
            .artName(request.getTitle())
            .caption(request.getCaption())
            .artType(request.getArtType())
            .file(file)
            .galleries(gallery)
            .build();
    artsRepository.save(art);

    for (AdminArtsRequestDto.ArtistInfo artistInfo : request.getArtistList()) {
      Artists artist =
          artistsRepository
              .findById(artistInfo.getArtistNo())
              .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTIST_NOT_FOUND));

      ArtArtistRel rel =
          ArtArtistRel.builder()
              .arts(art)
              .artists(artist)
              .description(artistInfo.getDescription())
              .build();
      artArtistRelRepository.save(rel);
    }
  }

  // 2. 작품 수정
  @Transactional
  public void update(Long artsNo, AdminArtsRequestDto request) {
    Arts art =
        artsRepository
            .findById(artsNo)
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTS_NOT_FOUND));

    Files file =
        filesRepository
            .findById(request.getFilesNo())
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.FILE_NOT_FOUND));

    art.updateArts(file, request.getTitle(), request.getCaption(), request.getArtType());

    artArtistRelRepository.deleteByArts(art);
    for (AdminArtsRequestDto.ArtistInfo artistInfo : request.getArtistList()) {
      Artists artist =
          artistsRepository
              .findById(artistInfo.getArtistNo())
              .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTIST_NOT_FOUND));

      ArtArtistRel rel =
          ArtArtistRel.builder()
              .arts(art)
              .artists(artist)
              .description(artistInfo.getDescription())
              .build();
      artArtistRelRepository.save(rel);
    }
  }

  // 3. 작품 삭제
  @Transactional
  public void delete(Long artsNo) {
    Arts art =
        artsRepository
            .findById(artsNo)
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTS_NOT_FOUND));
    artArtistRelRepository.deleteByArts(art);
    artsRepository.delete(art);
  }

  // 4. 작품 전체 조회
  @Transactional(readOnly = true)
  public List<AdminArtsListResponseDto> getAllArts() {
    return artsRepository.findAll().stream()
        .map(
            art ->
                AdminArtsListResponseDto.builder()
                    .artsNo(art.getArtsNo())
                    .artName(art.getArtName())
                    .caption(art.getCaption())
                    .artType(art.getArtType().name())
                    .fileUrl(art.getFile().getUrl())
                    .galleriesNo(art.getGalleries().getGalleriesNo())
                    .galleriesTitle(art.getGalleries().getTitle())
                    .build())
        .collect(Collectors.toList());
  }

  // 5. 작품 상세 조회
  @Transactional(readOnly = true)
  public AdminArtsDetailResponseDto getArtDetail(Long artsNo) {
    Arts art =
        artsRepository
            .findById(artsNo)
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTS_NOT_FOUND));

    List<AdminArtsDetailResponseDto.ArtistInfo> artistInfos =
        art.getArtArtistRels().stream()
            .map(
                rel ->
                    AdminArtsDetailResponseDto.ArtistInfo.builder()
                        .artistNo(rel.getArtists().getArtistsNo())
                        .name(rel.getArtists().getArtistName())
                        .description(rel.getDescription())
                        .build())
            .collect(Collectors.toList());

    return AdminArtsDetailResponseDto.builder()
        .artsNo(art.getArtsNo())
        .artName(art.getArtName())
        .caption(art.getCaption())
        .artType(art.getArtType().name())
        .fileUrl(art.getFile().getUrl())
        .galleriesNo(art.getGalleries().getGalleriesNo())
        .galleriesTitle(art.getGalleries().getTitle())
        .artists(artistInfos)
        .build();
  }
}
