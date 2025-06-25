package com.hanyang.arttherapy.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtsService {
  // 공통 메서드 분리(리팩토링)
  private final ArtsRepository artsRepository;
  private final FilesRepository filesRepository;
  private final ArtArtistRelRepository artArtistRelRepository;
  private final GalleriesRepository galleriesRepository;
  private final FileStorageService fileStorageService;

  /**
   * 통합 조회 메서드 - 최초 로딩 시 (연도와 기수 모두 없음): 현재 연도로 조회 - 연도와 기수가 모두 있으면 Year + Cohort 조회 - 연도만 있으면 Year
   * 조회 - 기수만 있으면 Cohort 조회
   */
  public Map<String, Object> getArtsByFilter(Integer year, Integer cohort, Long lastId) {
    List<ArtsListResponseDto> response;

    if (year == null && cohort == null) {
      // 최초 페이지 로딩 시 현재 연도로 조회
      year = LocalDate.now().getYear();
      response = getArtsByYear(year, lastId);

    } else if (year != null && cohort != null) {
      // 연도 + 기수 조회
      response = getArtsByYearAndCohort(year, cohort, lastId);
    } else if (year != null) {
      // 연도만 조회
      response = getArtsBySelectedYear(year, lastId);
    } else {
      // 기수만 조회
      response = getArtsByCohort(cohort, lastId);
    }

    // 응답 포맷 통일
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("content", response);
    result.put("lastId", response.isEmpty() ? null : response.get(response.size() - 1).artsNo());
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

      return ArtsResponseDto.of(
          arts, artistResponses, fileResponses.isEmpty() ? null : fileResponses.get(0));
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error getting art detail for artsNo {}: {}", artsNo, e.getMessage(), e); // 로깅 추가
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
        .findByStartDateBetween(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31))
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
      // 1. 중복 제거: artsNo 기준
      Map<Long, Arts> distinctArtsMap =
          artsList.stream()
              .collect(
                  Collectors.toMap(
                      Arts::getArtsNo, a -> a, (a1, a2) -> a1 // 중복 발생 시 앞의 값 유지
                      ));

      List<Arts> distinctArtsList = new ArrayList<>(distinctArtsMap.values());

      // 2. 모든 artsNo, filesNo 추출
      List<Long> artsNos = distinctArtsList.stream().map(Arts::getArtsNo).toList();

      List<Long> fileNos =
          distinctArtsList.stream()
              .map(Arts::getFile)
              .filter(Objects::nonNull)
              .map(Files::getFilesNo)
              .toList();

      // 3. 파일, 작가 관계 미리 조회하여 Map으로 캐싱
      Map<Long, Files> fileEntityMap =
          filesRepository.findAllById(fileNos).stream()
              .collect(Collectors.toMap(Files::getFilesNo, f -> f));

      Map<Long, List<ArtArtistRel>> artistRelMap =
          artArtistRelRepository.findWithArtistsByArtsNoIn(artsNos).stream()
              .collect(Collectors.groupingBy(rel -> rel.getArts().getArtsNo()));

      // 4. 작가 이름 가나다 정렬
      List<Arts> sortedList =
          distinctArtsList.stream()
              .sorted(
                  Comparator.comparing(
                      art ->
                          artistRelMap.getOrDefault(art.getArtsNo(), List.of()).stream()
                              .map(rel -> rel.getArtists().getArtistName())
                              .sorted(String.CASE_INSENSITIVE_ORDER)
                              .collect(Collectors.joining(", "))))
              .toList();

      // 5. DTO 매핑
      return sortedList.stream()
          .map(
              arts -> {
                Files fileEntity =
                    fileEntityMap.get(arts.getFile() != null ? arts.getFile().getFilesNo() : null);
                List<ArtArtistRel> rels = artistRelMap.getOrDefault(arts.getArtsNo(), List.of());

                String name = (fileEntity != null) ? fileEntity.getName() : null;
                String url =
                    (fileEntity != null)
                        ? fileStorageService.getFileUrl(fileEntity.getFilesNo())
                        : null;

                return ArtsListResponseDto.of(arts, name, url, rels);
              })
          .toList();

    } catch (Exception e) {
      log.error("Error mapping arts to DTO list: {}", e.getMessage(), e);
      throw new CustomException(ArtsExceptionType.ART_LOAD_FAILED);
    }
  }

  private List<com.hanyang.arttherapy.dto.response.FileResponseDto> getFileResponseDto(
      Long filesNo) {
    return filesRepository.findByFilesNoInAndUseYn(List.of(filesNo), true).stream()
        .map(
            file ->
                com.hanyang.arttherapy.dto.response.FileResponseDto.of(
                    file, fileStorageService.getFileUrl(file.getFilesNo()))) // 변경된 변수명 적용
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
