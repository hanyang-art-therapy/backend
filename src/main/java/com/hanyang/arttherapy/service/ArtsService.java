// package com.hanyang.arttherapy.service;
//
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.Comparator;
// import java.util.List;
// import java.util.stream.Collectors;
//
// import jakarta.persistence.EntityNotFoundException;
//
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.hanyang.arttherapy.domain.ArtArtistRel;
// import com.hanyang.arttherapy.domain.Arts;
// import com.hanyang.arttherapy.domain.Files;
// import com.hanyang.arttherapy.domain.Galleries;
// import com.hanyang.arttherapy.dto.response.*;
// import com.hanyang.arttherapy.repository.*;
//
// import lombok.RequiredArgsConstructor;
//
// @Service
// @RequiredArgsConstructor
// @Transactional
// public class ArtsService {
//  // 공통 메서드 분리(리팩토링)
//  private final ArtsRepository artsRepository;
//  private final FilesRepository filesRepository;
//  private final ArtArtistRelRepository artArtistRelRepository;
//  private final ReviewService reviewService;
//  private final GalleriesRepository galleriesRepository;
//  private final ArtistsRepository artistsRepository;
//
//  // 작품 상세 조회
//  public ArtsResponseDto getArtDetail(Long galleriesNo, Long artsNo) {
//    Arts arts = getArtById(artsNo);
//
//    GalleryResponseDto galleryResponse =
//        getGalleryResponseDto(arts.getGalleries().getGalleriesNo());
//
//    List<ArtArtistRel> artistRels = artArtistRelRepository.findByArts_ArtsNo(artsNo);
//    List<ArtistResponseDto> artistResponses = getArtistResponseDto(artistRels);
//
//    List<FileResponseDto> fileResponses = getFileResponseDto(arts.getFile().getFilesNo());
//    Pageable pageable = PageRequest.of(0, 5);
//    List<ReviewResponseDto> reviews = getReviewResponseDto(galleriesNo, artsNo, pageable);
//    String description = getDescription(artistRels);
//    String createdAt = getFormattedDate(arts);
//
//    return new ArtsResponseDto(
//        arts.getArtsNo(),
//        arts.getArtName(),
//        arts.getCaption(),
//        description,
//        createdAt,
//        galleryResponse,
//        artistResponses,
//        fileResponses.isEmpty() ? null : fileResponses.get(0),
//        reviews);
//  }
//
//  // 작품 전체 조회
//  public Page<ArtsListResponseDto> getArtsByYear(Long galleriesNo, Pageable pageable) {
//
//    // URL로 전달된 galleriesNo로 전시회 조회
//    Galleries gallery =
//        galleriesRepository
//            .findById(galleriesNo)
//            .orElseThrow(() -> new EntityNotFoundException("해당 전시회가 존재하지 않습니다."));
//
//    // 시스템 연도 가져오기
//    int currentYear = LocalDate.now().getYear();
//
//    // 전시회가 현재 연도에 해당하는지 체크
//    if (gallery.getStartDate().getYear() > currentYear
//        || gallery.getEndDate().getYear() < currentYear) {
//      throw new IllegalArgumentException("해당 전시회는 현재 연도에 개최되지 않았습니다.");
//    }
//
//    pageable = PageRequest.of(pageable.getPageNumber(), 9);
//
//    // 작품 조회
//    List<Arts> artsList = artsRepository.findByGalleries_GalleriesNo(galleriesNo);
//
//    // 정렬 (작가 이름 가나다 순)
//    List<Arts> sortedList = sortArtsByArtistNames(artsList);
//
//    // DTO 매핑
//    List<ArtsListResponseDto> dtoList =
//        sortedList.stream().map(this::mapToArtsListResponseDto).collect(Collectors.toList());
//
//    // 페이지네이션 처리
//    int start = (int) pageable.getOffset();
//    int end = Math.min((start + pageable.getPageSize()), dtoList.size());
//    List<ArtsListResponseDto> pageList = dtoList.subList(start, end);
//
//    return new PageImpl<>(pageList, pageable, dtoList.size());
//  }
//
//  // 연도별 조회 (드롭다운에서 연도만 선택했을 때)
//  public Page<ArtsListResponseDto> getArtsBySelectedYear(Integer year, Pageable pageable) {
//    // 해당 연도에 열린 모든 전시회 조회
//    List<Galleries> galleriesList =
//        galleriesRepository.findByStartDateBetween(
//            LocalDateTime.of(year, 1, 1, 0, 0), LocalDateTime.of(year, 12, 31, 23, 59));
//
//    pageable = PageRequest.of(pageable.getPageNumber(), 9);
//
//    // 작품 조회
//    List<Arts> artsList =
//        galleriesList.stream()
//            .flatMap(
//                gallery ->
//                    artsRepository.findByGalleries_GalleriesNo(gallery.getGalleriesNo()).stream())
//            .collect(Collectors.toList());
//
//    // 정렬 (작가 이름 가나다 순)
//    List<Arts> sortedList = sortArtsByArtistNames(artsList);
//
//    // DTO 매핑
//    List<ArtsListResponseDto> dtoList =
//        sortedList.stream().map(this::mapToArtsListResponseDto).collect(Collectors.toList());
//
//    // 페이지네이션 처리
//    int start = (int) pageable.getOffset();
//    int end = Math.min((start + pageable.getPageSize()), dtoList.size());
//    List<ArtsListResponseDto> pageList = dtoList.subList(start, end);
//
//    return new PageImpl<>(pageList, pageable, dtoList.size());
//  }
//
//  // 기수별 조회 (정렬 추가)
//  public Page<ArtsListResponseDto> getArtsByCohort(int cohort, Pageable pageable) {
//    // 모든 전시회의 작품을 조회
//    List<Arts> artsList = artsRepository.findAll();
//
//    // 기수에 맞는 작품 필터링
//    List<Arts> filteredList =
//        artsList.stream()
//            .filter(
//                art ->
//                    art.getArtArtistRels().stream()
//                        .anyMatch(rel -> rel.getArtists().getCohort() == cohort))
//            .collect(Collectors.toList());
//
//    // 정렬 (작가 이름 가나다 순)
//    List<Arts> sortedList = sortArtsByArtistNames(filteredList);
//
//    // DTO 매핑
//    List<ArtsListResponseDto> dtoList =
//        sortedList.stream().map(this::mapToArtsListResponseDto).collect(Collectors.toList());
//
//    return new PageImpl<>(dtoList, pageable, dtoList.size());
//  }
//
//  // 연도 + 기수 조회 (정렬 추가)
//  public Page<ArtsListResponseDto> getArtsByYearAndCohort(int year, int cohort, Pageable pageable)
// {
//    // 해당 연도에 열린 모든 전시회 조회
//    List<Galleries> galleriesList =
//        galleriesRepository.findByStartDateBetween(
//            LocalDateTime.of(year, 1, 1, 0, 0), LocalDateTime.of(year, 12, 31, 23, 59));
//
//    // 모든 전시회의 작품 조회
//    List<Arts> artsList =
//        galleriesList.stream()
//            .flatMap(
//                gallery ->
//                    artsRepository.findByGalleries_GalleriesNo(gallery.getGalleriesNo()).stream())
//            .collect(Collectors.toList());
//
//    // 기수에 맞는 작품 필터링
//    List<Arts> filteredList =
//        artsList.stream()
//            .filter(
//                art ->
//                    art.getArtArtistRels().stream()
//                        .anyMatch(rel -> rel.getArtists().getCohort() == cohort))
//            .collect(Collectors.toList());
//
//    // 정렬 (작가 이름 가나다 순)
//    List<Arts> sortedList = sortArtsByArtistNames(filteredList);
//
//    // DTO 매핑
//    List<ArtsListResponseDto> dtoList =
//        sortedList.stream().map(this::mapToArtsListResponseDto).collect(Collectors.toList());
//
//    return new PageImpl<>(dtoList, pageable, dtoList.size());
//  }
//
//  // 작품의 작가 이름을 기준으로 정렬하는 메서드
//  private List<Arts> sortArtsByArtistNames(List<Arts> artsList) {
//    return artsList.stream()
//        .sorted(Comparator.comparing(art -> getSortedArtistNames(art)))
//        .collect(Collectors.toList());
//  }
//
//  // 작가 이름들을 추출하여 정렬 후 하나의 문자열로 결합
//  private String getSortedArtistNames(Arts art) {
//    return art.getArtArtistRels().stream()
//        .map(rel -> rel.getArtists().getArtistName())
//        .sorted()
//        .collect(Collectors.joining(", "));
//  }
//
//  private Arts getArtById(Long artsNo) {
//    return artsRepository
//        .findByArtsNo(artsNo)
//        .orElseThrow(() -> new EntityNotFoundException("해당 작품을 찾을 수 없습니다."));
//  }
//
//  private ArtsListResponseDto mapToArtsListResponseDto(Arts arts) {
//    Files file =
//        filesRepository
//            .findById(arts.getFile().getFilesNo())
//            .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));
//
//    List<ArtArtistRel> artistRels = artArtistRelRepository.findByArts_ArtsNo(arts.getArtsNo());
//
//    return ArtsListResponseDto.of(arts, file, artistRels, arts.getGalleries().getGalleriesNo());
//  }
//
//  private List<ArtistResponseDto> getArtistResponseDto(List<ArtArtistRel> artistRels) {
//    return artistRels.stream()
//        .map(
//            rel ->
//                new ArtistResponseDto(
//                    rel.getArtists().getArtistName(),
//                    rel.getArtists().getStudentNo(),
//                    rel.getArtists().getCohort()))
//        .collect(Collectors.toList());
//  }
//
//  private List<FileResponseDto> getFileResponseDto(Long filesNo) {
//    return filesRepository.findByFilesNoInAndUseYn(List.of(filesNo), true).stream()
//        .map(file -> FileResponseDto.of(file, file.getUrl()))
//        .collect(Collectors.toList());
//  }
//
//  private List<ReviewResponseDto> getReviewResponseDto(
//      Long galleriesNo, Long artsNo, Pageable pageable) {
//    return reviewService.getReviews(galleriesNo, artsNo, pageable).getContent();
//  }
//
//  private String getDescription(List<ArtArtistRel> artistRels) {
//    return artistRels.isEmpty() ? "작품 설명이 없습니다." : artistRels.get(0).getDescription();
//  }
//
//  private String getFormattedDate(Arts arts) {
//    return arts.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
//  }
//
//  private GalleryResponseDto getGalleryResponseDto(Long galleriesNo) {
//    Galleries gallery =
//        galleriesRepository
//            .findById(galleriesNo)
//            .orElseThrow(() -> new IllegalArgumentException("해당 전시회가 존재하지 않습니다."));
//    return new GalleryResponseDto(gallery.getGalleriesNo());
//  }
// }
