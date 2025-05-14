package com.hanyang.arttherapy.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.ArtsExceptionType;
import com.hanyang.arttherapy.common.exception.exceptionType.FileSystemExceptionType;
import com.hanyang.arttherapy.common.exception.exceptionType.GalleryExceptionType;
import com.hanyang.arttherapy.domain.ArtArtistRel;
import com.hanyang.arttherapy.domain.Arts;
import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.dto.response.*;
import com.hanyang.arttherapy.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtsService {
  // 공통 메서드 분리(리팩토링)
  private final ArtsRepository artsRepository;
  private final FilesRepository filesRepository;
  private final ArtArtistRelRepository artArtistRelRepository;
  private final ReviewService reviewService;
  private final GalleriesRepository galleriesRepository;
  private final ArtistsRepository artistsRepository;

  // 작품 상세 조회
  public ArtsResponseDto getArtDetail(Long artsNo) {
    Arts arts = getArtById(artsNo);

    List<ArtArtistRel> artistRels = artArtistRelRepository.findByArts_ArtsNo(artsNo);
    List<ArtistResponseDto> artistResponses = getArtistResponseDto(artistRels);

    List<FileResponseDto> fileResponses = getFileResponseDto(arts.getFile().getFilesNo());

    String description = getDescription(artistRels);
    LocalDateTime createdAt = arts.getCreatedAt();

    return new ArtsResponseDto(
        arts.getArtsNo(),
        arts.getArtName(),
        arts.getCaption(),
        description,
        createdAt,
        artistResponses,
        fileResponses.isEmpty() ? null : fileResponses.get(0));
  }

  // 작품 전체 조회
  public List<ArtsListResponseDto> getArtsByYear(int year, Long lastId) {

    // 해당 연도의 갤러리 조회 (1년에 한 번만 개최됨)
    Galleries gallery =
        galleriesRepository
            .findByStartDateBetween(
                LocalDateTime.of(year, 1, 1, 0, 0), LocalDateTime.of(year, 12, 31, 23, 59))
            .stream()
            .findFirst()
            .orElseThrow(() -> new CustomException(GalleryExceptionType.GALLERY_NOT_FOUND));

    // 작품 조회
    List<Arts> artsList =
        (lastId == null)
            ? artsRepository.findTop9ByGalleries_GalleriesNoAndArtsNoGreaterThanOrderByArtsNoAsc(
                gallery.getGalleriesNo(), 0L)
            : artsRepository.findTop9ByGalleries_GalleriesNoAndArtsNoGreaterThanOrderByArtsNoAsc(
                gallery.getGalleriesNo(), lastId);

    // 정렬 (작가 이름 가나다 순)
    List<Arts> sortedList = sortArtsByArtistNames(artsList);

    // DTO 매핑
    return sortedList.stream().map(this::mapToArtsListResponseDto).collect(Collectors.toList());
  }

  // 연도별 조회 (드롭다운에서 연도만 선택했을 때)
  public List<ArtsListResponseDto> getArtsBySelectedYear(Integer year, Long lastId) {
    // 해당 연도에 열린 모든 전시회 조회
    Galleries gallery =
        galleriesRepository
            .findByStartDateBetween(
                LocalDateTime.of(year, 1, 1, 0, 0), LocalDateTime.of(year, 12, 31, 23, 59))
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당 연도에 전시회가 존재하지 않습니다."));

    // 작품 조회
    List<Arts> artsList =
        (lastId == null)
            ? artsRepository.findTop9ByGalleries_GalleriesNoAndArtsNoGreaterThanOrderByArtsNoAsc(
                gallery.getGalleriesNo(), 0L)
            : artsRepository.findTop9ByGalleries_GalleriesNoAndArtsNoGreaterThanOrderByArtsNoAsc(
                gallery.getGalleriesNo(), lastId);

    // 정렬 (작가 이름 가나다 순)
    List<Arts> sortedList = sortArtsByArtistNames(artsList);

    // DTO 매핑
    return sortedList.stream()
        .map(art -> mapToArtsListResponseDto(art))
        .collect(Collectors.toList());
  }

  // 기수별 조회
  public List<ArtsListResponseDto> getArtsByCohort(int cohort, Long lastId) {
    // 기수에 해당하는 작품 9개씩
    List<Arts> artsList =
        artsRepository.findTop9ByArtArtistRels_Artists_CohortAndArtsNoGreaterThanOrderByArtsNoAsc(
            cohort, lastId == null ? 0L : lastId);

    // 정렬 (작가 이름 가나다 순)
    List<Arts> sortedList = sortArtsByArtistNames(artsList);

    // DTO 매핑
    return sortedList.stream().map(this::mapToArtsListResponseDto).collect(Collectors.toList());
  }

  // 연도 + 기수 조회 (정렬 추가)
  public List<ArtsListResponseDto> getArtsByYearAndCohort(int year, int cohort, Long lastId) {
    // 해당 연도에 열린 모든 전시회 조회
    Galleries gallery =
        galleriesRepository
            .findByStartDateBetween(
                LocalDateTime.of(year, 1, 1, 0, 0), LocalDateTime.of(year, 12, 31, 23, 59))
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당 연도에 전시회가 존재하지 않습니다."));

    // 각 전시회에서 작품을 조회
    List<Arts> artsList =
        artsRepository.findTop9ByArtArtistRels_Artists_CohortAndArtsNoGreaterThanOrderByArtsNoAsc(
            cohort, lastId == null ? 0L : lastId);

    // 정렬 (작가 이름 가나다 순)
    List<Arts> sortedList = sortArtsByArtistNames(artsList);

    // DTO 매핑
    return sortedList.stream().map(this::mapToArtsListResponseDto).collect(Collectors.toList());
  }

  // 작품의 작가 이름을 기준으로 정렬하는 메서드
  private List<Arts> sortArtsByArtistNames(List<Arts> artsList) {
    return artsList.stream()
        .sorted(Comparator.comparing(this::getSortedArtistNames))
        .collect(Collectors.toList());
  }

  // 작가 이름들을 추출하여 정렬 후 하나의 문자열로 결합
  private String getSortedArtistNames(Arts art) {
    return art.getArtArtistRels().stream()
        .map(rel -> rel.getArtists().getArtistName())
        .sorted()
        .collect(Collectors.joining(", "));
  }

  private Arts getArtById(Long artsNo) {
    return artsRepository
        .findByArtsNo(artsNo)
        .orElseThrow(() -> new CustomException(ArtsExceptionType.ART_NOT_FOUND));
  }

  private ArtsListResponseDto mapToArtsListResponseDto(Arts arts) {
    Files file =
        filesRepository
            .findById(arts.getFile().getFilesNo())
            .orElseThrow(() -> new CustomException(FileSystemExceptionType.FILE_NOT_FOUND));

    List<ArtArtistRel> artistRels = artArtistRelRepository.findByArts_ArtsNo(arts.getArtsNo());

    return ArtsListResponseDto.of(arts, file, artistRels);
  }

  private List<ArtistResponseDto> getArtistResponseDto(List<ArtArtistRel> artistRels) {
    return artistRels.stream()
        .map(
            rel ->
                new ArtistResponseDto(
                    rel.getArtists().getArtistName(),
                    rel.getArtists().getStudentNo(),
                    rel.getArtists().getCohort()))
        .collect(Collectors.toList());
  }

  private List<FileResponseDto> getFileResponseDto(Long filesNo) {
    return filesRepository.findByFilesNoInAndUseYn(List.of(filesNo), true).stream()
        .map(file -> FileResponseDto.of(file, file.getUrl()))
        .collect(Collectors.toList());
  }

  private String getDescription(List<ArtArtistRel> artistRels) {
    return artistRels.isEmpty() ? "작품 설명이 없습니다." : artistRels.get(0).getDescription();
  }

  private GalleryResponseDto getGalleryResponseDto(Long galleriesNo) {
    Galleries gallery =
        galleriesRepository
            .findById(galleriesNo)
            .orElseThrow(() -> new CustomException(GalleryExceptionType.GALLERY_NOT_FOUND));
    return new GalleryResponseDto(gallery.getGalleriesNo());
  }
}
