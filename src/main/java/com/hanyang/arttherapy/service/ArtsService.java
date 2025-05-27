package com.hanyang.arttherapy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.ArtsExceptionType;
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

  /**
   * 통합 조회 메서드 - 최초 로딩 시 (연도와 기수 모두 없음): 현재 연도로 조회 - 연도와 기수가 모두 있으면 Year + Cohort 조회 - 연도만 있으면 Year
   * 조회 - 기수만 있으면 Cohort 조회
   */
  public Map<String, Object> getArtsByFilter(Integer year, Integer cohort, Long lastId) {
    List<ArtsListResponseDto> response;
    Long totalElements = 0L;

    if (year == null && cohort == null) {
      // 최초 페이지 로딩 시 현재 연도로 조회
      year = LocalDate.now().getYear();
      response = getArtsByYear(year, lastId);
      totalElements =
          artsRepository.countByGalleries_GalleriesNo(findGalleryByYear(year).getGalleriesNo());

    } else if (year != null && cohort != null) {
      // 연도 + 기수 조회
      response = getArtsByYearAndCohort(year, cohort, lastId);
      totalElements =
          artsRepository.countByGalleries_GalleriesNoAndArtArtistRels_Artists_Cohort(
              findGalleryByYear(year).getGalleriesNo(), cohort);
    } else if (year != null) {
      // 연도만 조회
      response = getArtsBySelectedYear(year, lastId);
      totalElements =
          artsRepository.countByGalleries_GalleriesNo(findGalleryByYear(year).getGalleriesNo());
    } else {
      // 기수만 조회
      response = getArtsByCohort(cohort, lastId);
      totalElements = artsRepository.countByArtArtistRels_Artists_Cohort(cohort);
    }

    // 응답 포맷 통일
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("content", response);
    result.put("lastId", response.isEmpty() ? null : response.get(response.size() - 1).artsNo());
    result.put("totalElements", totalElements);
    result.put("hasNext", !response.isEmpty() && response.size() == 9);

    return result;
  }

  // 작품 상세 조회
  public ArtsResponseDto getArtDetail(Long artsNo) {
    try {
      Arts arts =
          artsRepository
              .findById(artsNo)
              .orElseThrow(() -> new CustomException(ArtsExceptionType.ART_NOT_FOUND));

      List<ArtArtistRel> artistRels = artArtistRelRepository.findByArts_ArtsNo(artsNo);
      List<ArtArtistRelResponseDto> artistResponses =
          artistRels.stream().map(ArtArtistRelResponseDto::of).collect(Collectors.toList());

      List<FileResponseDto> fileResponses = getFileResponseDto(arts.getFile().getFilesNo());
      LocalDateTime createdAt = arts.getCreatedAt();

      return ArtsResponseDto.of(
          arts, createdAt, artistResponses, fileResponses.isEmpty() ? null : fileResponses.get(0));
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      throw new CustomException(ArtsExceptionType.ART_LOAD_FAILED);
    }
  }

  // 작품 전체 조회
  public List<ArtsListResponseDto> getArtsByYear(int year, Long lastId) {
    Galleries gallery = findGalleryByYear(year);

    if (gallery == null) {
      return List.of();
    }

    List<Arts> artsList = fetchArts(gallery.getGalleriesNo(), lastId);
    return mapToDtoList(artsList);
  }

  // 연도별 조회 (드롭다운에서 연도만 선택했을 때)
  public List<ArtsListResponseDto> getArtsBySelectedYear(Integer year, Long lastId) {
    Galleries gallery = findGalleryByYear(year);
    if (gallery == null) {
      return List.of();
    }

    List<Arts> artsList = fetchArts(gallery.getGalleriesNo(), lastId);
    return mapToDtoList(artsList);
  }

  // 기수별 조회
  public List<ArtsListResponseDto> getArtsByCohort(int cohort, Long lastId) {
    List<Arts> artsList =
        artsRepository.findTop9ByArtArtistRels_Artists_CohortAndArtsNoGreaterThanOrderByArtsNoAsc(
            cohort, lastId == null ? 0L : lastId);
    return mapToDtoList(artsList);
  }

  // 연도 + 기수 조회 (정렬 추가)
  public List<ArtsListResponseDto> getArtsByYearAndCohort(int year, int cohort, Long lastId) {
    Galleries gallery = findGalleryByYear(year);

    if (gallery == null) {
      return List.of();
    }

    List<Arts> artsList =
        artsRepository
            .findTop9ByGalleries_GalleriesNoAndArtArtistRels_Artists_CohortAndArtsNoGreaterThanOrderByArtsNoAsc(
                gallery.getGalleriesNo(), cohort, lastId == null ? 0L : lastId);

    return mapToDtoList(artsList);
  }

  public List<Integer> getAvailableYears() {
    return galleriesRepository.findAll().stream()
        .map(g -> g.getStartDate().getYear())
        .distinct()
        .sorted()
        .toList();
  }

  public List<Integer> getAvailableCohorts(Integer year) {
    int targetYear = (year != null) ? year : LocalDate.now().getYear();
    Galleries galleries = findGalleryByYear(targetYear);
    if (galleries == null) return List.of();
    return artArtistRelRepository.findDistinctCohortsByGalleriesNo(galleries.getGalleriesNo());
  }

  // 연도에 해당하는 전시회 조회
  private Galleries findGalleryByYear(int year) {
    return galleriesRepository
        .findByStartDateBetween(
            LocalDateTime.of(year, 1, 1, 0, 0), LocalDateTime.of(year, 12, 31, 23, 59))
        .stream()
        .findFirst()
        .orElse(null);
  }

  // 전시회 번호에 맞춰 작품을 조회
  private List<Arts> fetchArts(Long galleriesNo, Long lastId) {
    return (lastId == null)
        ? artsRepository.findTop9ByGalleries_GalleriesNoAndArtsNoGreaterThanOrderByArtsNoAsc(
            galleriesNo, 0L)
        : artsRepository.findTop9ByGalleries_GalleriesNoAndArtsNoGreaterThanOrderByArtsNoAsc(
            galleriesNo, lastId);
  }

  private List<ArtsListResponseDto> mapToDtoList(List<Arts> artsList) {
    if (artsList.isEmpty()) return List.of();

    try {
      // 1. 모든 artsNo, filesNo 추출
      List<Long> artsNos = artsList.stream().map(Arts::getArtsNo).toList();

      List<Long> fileNos = artsList.stream().map(a -> a.getFile().getFilesNo()).toList();

      // 2. 파일, 작가 관계 미리 조회하여 Map으로 캐싱
      Map<Long, Files> fileMap =
          filesRepository.findAllById(fileNos).stream()
              .collect(Collectors.toMap(Files::getFilesNo, f -> f));

      Map<Long, List<ArtArtistRel>> artistRelMap =
          artArtistRelRepository.findWithArtistsByArtsNoIn(artsNos).stream()
              .collect(Collectors.groupingBy(rel -> rel.getArts().getArtsNo()));

      // 3. 정렬 (작가 이름 가나다 순 기준)
      List<Arts> sortedList =
          artsList.stream()
              .sorted(
                  Comparator.comparing(
                      art ->
                          artistRelMap.getOrDefault(art.getArtsNo(), List.of()).stream()
                              .map(rel -> rel.getArtists().getArtistName())
                              .sorted(String.CASE_INSENSITIVE_ORDER)
                              .collect(Collectors.joining(", "))))
              .toList();

      // 4. DTO 매핑
      return sortedList.stream()
          .map(
              arts -> {
                Files file = fileMap.get(arts.getFile().getFilesNo());
                List<ArtArtistRel> rels = artistRelMap.getOrDefault(arts.getArtsNo(), List.of());
                return ArtsListResponseDto.of(arts, file, rels);
              })
          .toList();
    } catch (Exception e) {
      throw new CustomException(ArtsExceptionType.ART_LOAD_FAILED);
    }
  }

  private List<FileResponseDto> getFileResponseDto(Long filesNo) {
    return filesRepository.findByFilesNoInAndUseYn(List.of(filesNo), true).stream()
        .map(file -> FileResponseDto.of(file, file.getUrl()))
        .collect(Collectors.toList());
  }

  private GalleryResponseDto getGalleryResponseDto(Long galleriesNo) {
    Galleries gallery =
        galleriesRepository
            .findById(galleriesNo)
            .orElseThrow(() -> new CustomException(GalleryExceptionType.GALLERY_NOT_FOUND));
    return new GalleryResponseDto(gallery.getGalleriesNo());
  }
}
