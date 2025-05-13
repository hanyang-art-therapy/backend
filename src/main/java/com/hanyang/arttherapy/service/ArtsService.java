package com.hanyang.arttherapy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
  private final ArtArtistRelRepository artArtistRelRepository;
  private final ReviewService reviewService;
  private final GalleriesRepository galleriesRepository;
  private final ArtistsRepository artistsRepository;

  // 작품 상세 조회
  public ArtsResponseDto getArtDetail(Long galleriesNo, Long artsNo) {
    Arts arts = getArtById(artsNo);

    GalleryResponseDto galleryResponse = getGalleryResponseDto(arts.getGalleriesNo());

    List<ArtArtistRel> artistRels = artArtistRelRepository.findByArtsNo(artsNo);
    List<ArtistResponseDto> artistResponses = getArtistResponseDto(artistRels);
    List<FileResponseDto> fileResponses = getFileResponseDto(arts.getFilesNo());
    Pageable pageable = PageRequest.of(0, 5);
    List<ReviewResponseDto> reviews = getReviewResponseDto(artsNo, pageable);
    String description = getDescription(artistRels);
    String createdAt = getFormattedDate(arts);

    return new ArtsResponseDto(
        arts.getArtsNo(),
        arts.getArtName(),
        arts.getCaption(),
        description,
        createdAt,
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
  public Page<ArtsListResponseDto> getArtsByYear(Integer year, Pageable pageable) {

    pageable = PageRequest.of(pageable.getPageNumber(), 9);

    // 만약 year가 null로 넘어오면 시스템 연도로 대체
    if (year == null) {
      year = LocalDate.now().getYear();
    }

    // 해당 연도의 모든 전시회 조회
    List<Galleries> galleriesList = findByYear(year);

    // 전시회에 속한 모든 작품 조회
    List<Arts> artsList =
        galleriesList.stream()
            .flatMap(gallery -> artsRepository.findByGalleriesNo(gallery.getGalleriesNo()).stream())
            .collect(Collectors.toList());

    // 정렬 (작가 이름 가나다 순)
    List<Arts> sortedList =
        artsList.stream()
            .sorted(Comparator.comparing(Arts::getArtName))
            .collect(Collectors.toList());

    // 작품 정보를 DTO로 변환
    List<ArtsListResponseDto> dtoList =
        sortedList.stream().map(this::mapToArtsListResponseDto).collect(Collectors.toList());

    // 페이지네이션 처리
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), dtoList.size());
    List<ArtsListResponseDto> pageList = dtoList.subList(start, end);

    // 페이징 처리된 결과 반환
    return new PageImpl<>(pageList, pageable, dtoList.size());
  }

  // 기수별 조회
  public Page<ArtsListResponseDto> getArtsByCohort(int cohort, Pageable pageable) {
    // JPQL로 페이징 처리된 결과를 바로 가져오기
    Page<Arts> artsPage = artsRepository.findByCohort(cohort, pageable);

    // DTO 변환
    List<ArtsListResponseDto> dtoList =
        artsPage.stream().map(this::mapToArtsListResponseDto).collect(Collectors.toList());

    return new PageImpl<>(dtoList, pageable, artsPage.getTotalElements());
  }

  // 연도 + 기수 조회
  public Page<ArtsListResponseDto> getArtsByYearAndCohort(int year, int cohort, Pageable pageable) {
    List<Galleries> galleriesList = findByYear(year);

    // JPQL로 페이징 처리된 결과를 바로 가져오기
    List<ArtsListResponseDto> dtoList =
        galleriesList.stream()
            .flatMap(
                gallery ->
                    artsRepository
                        .findByGalleriesNoAndCohort(gallery.getGalleriesNo(), cohort, pageable)
                        .stream())
            .map(this::mapToArtsListResponseDto)
            .collect(Collectors.toList());

    return new PageImpl<>(dtoList, pageable, dtoList.size());
  }

  private Arts getArtById(Long artsNo) {
    return artsRepository
        .findByArtsNo(artsNo)
        .orElseThrow(() -> new EntityNotFoundException("해당 작품을 찾을 수 없습니다."));
  }

  private ArtsListResponseDto mapToArtsListResponseDto(Arts arts) {
    // 파일 조회
    Files file =
        filesRepository
            .findById(arts.getFilesNo())
            .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

    // 작가 조회
    ArtArtistRel artistRel =
        artArtistRelRepository.findByArtsNo(arts.getArtsNo()).stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("작가를 찾을 수 없습니다."));

    // 작가 정보 매핑
    ArtistResponseDto artistDto =
        artistsRepository
            .findById(artistRel.getArtistsNo())
            .map(
                artist ->
                    new ArtistResponseDto(
                        artist.getArtistName(), artist.getStudentNo(), artist.getCohort()))
            .orElseThrow(() -> new IllegalArgumentException("작가 정보를 찾을 수 없습니다."));

    // ArtistResponse로 변환
    ArtsListResponseDto.ArtistResponse artistResponse =
        new ArtsListResponseDto.ArtistResponse(artistDto.artistName(), artistDto.cohort());

    return ArtsListResponseDto.of(arts, file, artistResponse);
  }

  private List<ArtistResponseDto> getArtistResponseDto(List<ArtArtistRel> artistRels) {
    return artistRels.stream()
        .map(
            rel -> {
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

  private List<ReviewResponseDto> getReviewResponseDto(Long artsNo, Pageable pageable) {
    return reviewService.getReviews(artsNo, pageable).getContent();
  }

  private String getDescription(List<ArtArtistRel> artistRels) {
    return artistRels.isEmpty() ? "작품 설명이 없습니다." : artistRels.get(0).getDescription();
  }

  private String getFormattedDate(Arts arts) {
    return arts.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
  }

  private GalleryResponseDto getGalleryResponseDto(Long galleriesNo) {
    Galleries gallery =
        galleriesRepository
            .findById(galleriesNo)
            .orElseThrow(() -> new IllegalArgumentException("해당 전시회가 존재하지 않습니다."));
    return new GalleryResponseDto(gallery.getGalleriesNo());
  }
}
