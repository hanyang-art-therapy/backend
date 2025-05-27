package com.hanyang.arttherapy.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.AdminArtsExceptionType;
import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.dto.request.AdminArtsPatchRequestDto;
import com.hanyang.arttherapy.dto.request.AdminArtsRequestDto;
import com.hanyang.arttherapy.dto.response.AdminArtsDetailResponseDto;
import com.hanyang.arttherapy.dto.response.AdminArtsListResponseDto;
import com.hanyang.arttherapy.dto.response.CommonScrollResponse;
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

  //  작품 등록
  @Transactional
  public String register(AdminArtsRequestDto request) {
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
            .artName(request.getArtName())
            .caption(request.getCaption())
            .artType(request.getArtType())
            .file(file)
            .galleries(gallery)
            .coDescription(request.getCoDescription())
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

    return "작품 등록에 성공했습니다";
  }

  // 작품 수정
  @Transactional
  public String update(Long artsNo, AdminArtsPatchRequestDto request) {
    Arts art =
        artsRepository
            .findById(artsNo)
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTS_NOT_FOUND));

    if (request.getFilesNo() != null) {
      Files file =
          filesRepository
              .findById(request.getFilesNo())
              .orElseThrow(() -> new CustomException(AdminArtsExceptionType.FILE_NOT_FOUND));
      art.updateFile(file);
    }
    if (request.getArtName() != null) art.updateTitle(request.getArtName());
    if (request.getCaption() != null) art.updateCaption(request.getCaption());
    if (request.getArtType() != null) art.updateArtType(request.getArtType());
    if (request.getCoDescription() != null) art.updateCoDescription(request.getCoDescription());
    if (request.getGalleriesNo() != null) {
      Galleries gallery =
          galleriesRepository
              .findById(request.getGalleriesNo())
              .orElseThrow(() -> new CustomException(AdminArtsExceptionType.GALLERY_NOT_FOUND));
      art.updateGallery(gallery);
    }

    if (request.getArtistList() != null) {
      artArtistRelRepository.deleteByArts(art);
      for (AdminArtsPatchRequestDto.ArtistInfo artistInfo : request.getArtistList()) {
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
    return "작품 수정에 성공했습니다";
  }

  // 작품 삭제
  @Transactional
  public String delete(Long artsNo) {
    Arts art =
        artsRepository
            .findById(artsNo)
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTS_NOT_FOUND));
    artArtistRelRepository.deleteByArts(art);
    artsRepository.delete(art);
    return "작품 삭제에 성공했습니다";
  }

  // 전체 작품 조회
  @Transactional(readOnly = true)
  public List<AdminArtsListResponseDto> getAllArts() {
    return artsRepository.findAll().stream().map(this::toListDto).toList();
  }

  // 상세 조회
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
            .toList();

    return AdminArtsDetailResponseDto.builder()
        .artsNo(art.getArtsNo())
        .artName(art.getArtName())
        .caption(art.getCaption())
        .artType(art.getArtType().name())
        .fileUrl(art.getFile().getUrl())
        .galleriesNo(art.getGalleries().getGalleriesNo())
        .galleriesTitle(art.getGalleries().getTitle())
        .coDescription(art.getCoDescription())
        .artists(artistInfos)
        .build();
  }

  // 키워드 검색 (전체 + 작가명)
  @Transactional(readOnly = true)
  public List<AdminArtsListResponseDto> searchArts(String keyword) {
    return artsRepository.findByArtNameOrArtistNameContaining(keyword).stream()
        .map(this::toListDto)
        .toList();
  }

  //  무한스크롤 조회 (검색 + 전체)
  @Transactional(readOnly = true)
  public CommonScrollResponse<AdminArtsListResponseDto> searchArtsWithScroll(
      String keyword, Long lastId, int size) {
    Pageable pageable = PageRequest.of(0, size);
    List<Arts> arts = artsRepository.searchArtsWithCursor(keyword, lastId, pageable);

    List<AdminArtsListResponseDto> content = arts.stream().map(this::toListDto).toList();

    Long nextCursor = content.isEmpty() ? null : content.get(content.size() - 1).getArtsNo();
    boolean hasNext = arts.size() == size;

    return new CommonScrollResponse<>(content, nextCursor, hasNext);
  }

  // 공통 변환 메서드
  private AdminArtsListResponseDto toListDto(Arts art) {
    return AdminArtsListResponseDto.builder()
        .artsNo(art.getArtsNo())
        .artName(art.getArtName())
        .artType(art.getArtType().name())
        .galleriesNo(art.getGalleries().getGalleriesNo())
        .galleriesTitle(art.getGalleries().getTitle())
        .build();
  }
}
