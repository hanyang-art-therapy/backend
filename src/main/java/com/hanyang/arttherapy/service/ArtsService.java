package com.hanyang.arttherapy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  private final UserRepository userRepository;
  private final ArtArtistRelRepository artArtistRelRepository;
  private final ReviewService reviewService;
  private final GalleriesRepository galleriesRepository;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
  private final ArtistsRepository artistsRepository;

  // 작품 상세 조회
  public ArtsResponseDto getArtDetail(Long galleriesNo, Long artsNo) {
    Arts arts = getArtById(artsNo);

    GalleryResponseDto galleryResponse = getGalleryResponseDto(arts.getGalleriesNo());
    List<ArtArtistRel> artistRels = artArtistRelRepository.findByArtsNo(artsNo);
    List<ArtistResponseDto> artistResponses = getArtistResponseDto(artistRels);
    List<FileResponseDto> fileResponses = getFileResponseDto(arts.getFilesNo());
    List<ReviewResponseDto> reviews = getReviewResponseDto(artsNo);
    String description = getDescription(artistRels);
    String formattedDate = getFormattedDate(arts);

    return new ArtsResponseDto(
        arts.getArtsNo(),
        arts.getArtName(),
        arts.getCaption(),
        description,
        formattedDate,
        galleryResponse,
        artistResponses,
        fileResponses,
        reviews);
  }

  // 연도에 맞는 전시회 조회
  public List<Galleries> findByYear(int year) {
    LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0);
    LocalDateTime end = LocalDateTime.of(year, 12, 31, 23, 59);

    return galleriesRepository.findByStartDateBetween(start, end);
  }

  // 작품 전체 조회
  public List<ArtsListResponseDto> getArtsByYear(int year) {
    List<Galleries> galleriesList = findByYear(year);

    List<Arts> artsList =
        galleriesList.stream()
            .flatMap(gallery -> artsRepository.findByGalleriesNo(gallery.getGalleriesNo()).stream())
            .collect(Collectors.toList());

    return mapToDtoList(artsList, year);
  }

  // 작품 리스트 매핑
  private List<ArtsListResponseDto> mapToDtoList(List<Arts> artsList, int year) {
    return artsList.stream()
        .map(
            arts -> {
              String thumbnailUrl = getThumbnailUrl(arts.getFilesNo());
              List<String> artistInfoList = getArtistInfoList(arts.getArtsNo());

              Galleries gallery =
                  galleriesRepository
                      .findById(arts.getGalleriesNo())
                      .orElseThrow(() -> new IllegalArgumentException("해당 전시회가 존재하지 않습니다."));

              return new ArtsListResponseDto(
                  arts.getArtsNo(),
                  arts.getArtName(),
                  gallery.getStartDate().getYear(),
                  thumbnailUrl != null ? thumbnailUrl : "No Thumbnail",
                  String.join(", ", artistInfoList),
                  new GalleryResponseDto(gallery.getGalleriesNo()));
            })
        .sorted(Comparator.comparing(ArtsListResponseDto::artistInfo))
        .collect(Collectors.toList());
  }

  // 기수별 조회
  public List<ArtsListResponseDto> getArtsByCohort(int cohort) {
    List<Arts> artsList = artsRepository.findByCohort(cohort);
    return mapToDtoList(artsList, LocalDate.now().getYear());
  }

  // 연도 + 기수 조회
  public List<ArtsListResponseDto> getArtsByYearAndCohort(int year, int cohort) {
    // 연도별로 전시회 조회
    List<Galleries> galleriesList = findByYear(year);

    // 전시회 번호와 기수를 조합하여 작품 조회
    List<Arts> artsList =
        galleriesList.stream()
            .flatMap(
                gallery ->
                    artsRepository.findByCohort(cohort).stream()
                        .filter(arts -> arts.getGalleriesNo().equals(gallery.getGalleriesNo())))
            .collect(Collectors.toList());

    return mapToDtoList(artsList, LocalDate.now().getYear());
  }

  // 키워드 검색
  public List<ArtsListResponseDto> searchArtsByName(String keyword) {
    List<Arts> artsList = artsRepository.findByArtNameContaining(keyword);
    return mapToDtoList(artsList, LocalDate.now().getYear());
  }

  private Arts getArtById(Long artsNo) {
    return artsRepository
        .findByArtsNo(artsNo)
        .orElseThrow(() -> new EntityNotFoundException("해당 작품을 찾을 수 없습니다."));
  }

  private List<ArtistResponseDto> getArtistResponseDto(List<ArtArtistRel> artistRels) {
    return artistRels.stream()
        .map(
            rel -> {
              // artistsNo를 통해 직접 조회
              return artistsRepository
                  .findById(rel.getArtistsNo())
                  .map(
                      artist ->
                          new ArtistResponseDto(
                              artist.getArtistName(), artist.getStudentNo(), artist.getCohort()))
                  .orElseThrow(() -> new EntityNotFoundException("작가 정보를 찾을 수 없습니다."));
            })
        .collect(Collectors.toList());
  }

  private List<FileResponseDto> getFileResponseDto(Long filesNo) {
    return filesRepository.findByFilesNoInAndUseYn(List.of(filesNo), true).stream()
        .map(file -> FileResponseDto.of(file, file.getUrl()))
        .collect(Collectors.toList());
  }

  private List<ReviewResponseDto> getReviewResponseDto(Long artsNo) {
    Pageable pageRequest = PageRequest.of(0, 5);
    return reviewService.getReviews(artsNo, pageRequest).getContent();
  }

  private String getDescription(List<ArtArtistRel> artistRels) {
    return artistRels.isEmpty() ? "작품 설명이 없습니다." : artistRels.get(0).getDescription();
  }

  private String getFormattedDate(Arts arts) {
    return arts.getCreatedAt().format(formatter);
  }

  private GalleryResponseDto getGalleryResponseDto(Long galleriesNo) {
    Galleries gallery =
        galleriesRepository
            .findById(galleriesNo)
            .orElseThrow(() -> new IllegalArgumentException("해당 전시회가 존재하지 않습니다."));

    return new GalleryResponseDto(gallery.getGalleriesNo());
  }

  private List<String> getArtistInfoList(Long artsNo) {
    return artArtistRelRepository.findByArtsNo(artsNo).stream()
        .map(
            rel ->
                userRepository
                    .findById(rel.getArtistsNo())
                    .map(user -> user.getUserName() + " " + user.getStudentNo())
                    .orElse("알 수 없음"))
        .sorted()
        .collect(Collectors.toList());
  }

  private String getThumbnailUrl(Long filesNo) {
    return filesRepository.findById(filesNo).map(Files::getUrl).orElse(null);
  }
}
