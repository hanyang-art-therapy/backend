package com.hanyang.arttherapy.service;

import static com.hanyang.arttherapy.domain.QArtists.artists;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.*;
import com.hanyang.arttherapy.common.exception.exceptionType.*;
import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.dto.request.*;
import com.hanyang.arttherapy.dto.response.artistResponse.ArtistResponseDto;
import com.hanyang.arttherapy.dto.response.artistResponse.ArtistScrollResponseDto;
import com.hanyang.arttherapy.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ArtistsService {

  private final ArtistsRepository artistsRepository;

  public String registerArtist(ArtistRequestDto dto) {
    isStudentNoDuplicate(dto.studentNo());
    Artists artist = convertToEntity(dto);
    saveArtist(artist);
    return "작가등록 성공";
  }

  // 작가 이름/학번 필터링(무한스크롤)
  public ArtistScrollResponseDto searchArtists(
      String filter, String keyword, Long lastNo, int size) {
    // 필터와 키워드가 없으면 전체 작가 리스트 반환
    if ((filter == null || filter.isBlank()) && (keyword == null || keyword.isBlank())) {
      List<Artists> artists = artistsRepository.findAll();
      List<ArtistResponseDto> dtos =
          artists.stream().map(ArtistResponseDto::of).collect(Collectors.toList());

      Long newLastNo = artists.isEmpty() ? null : artists.get(artists.size() - 1).getArtistsNo();
      boolean hasNext = artists.size() == size;

      return new ArtistScrollResponseDto(dtos, newLastNo, hasNext);
    }

    // 하나라도 비어있으면 예외
    if (filter == null || filter.isBlank()) {
      throw new CustomException(FilteringException.INVALID_REQUEST_FILTER);
    }
    if (keyword == null || keyword.isBlank()) {
      throw new CustomException(FilteringException.INVALID_REQUEST_KEYWORD);
    }

    // 검색 조건이 있으면 검색
    List<Artists> artists =
        artistsRepository.searchByArtistNameOrStudentNo(filter, keyword, lastNo, size);

    if (artists.isEmpty()) {
      throw new CustomException(FilteringException.NO_SEARCH_RESULT);
    }

    List<ArtistResponseDto> dtos = artists.stream().map(ArtistResponseDto::of).toList();

    Long newLastNo = artists.isEmpty() ? null : artists.get(artists.size() - 1).getArtistsNo();
    boolean hasNext = artists.size() == size;

    return new ArtistScrollResponseDto(dtos, newLastNo, hasNext);
  }

  public ArtistResponseDto getArtist(Long artistNo) {
    return ArtistResponseDto.of(findArtistById(artistNo));
  }

  public String updateArtist(long artistsNo, ArtistRequestDto dto) {
    Artists artist = findArtistById(artistsNo);

    Optional.ofNullable(dto.studentNo())
        .filter(newStudentNo -> !newStudentNo.equals(artist.getStudentNo()))
        .ifPresent(this::isStudentNoDuplicate);

    updateArtistInfo(artist, dto);
    artistsRepository.save(artist);

    return "작가 수정이 완료되었습니다";
  }

  public String deleteArtist(Long artistsNo) {
    Artists artist = findArtistById(artistsNo);
    artistsRepository.delete(artist);
    return "작가 삭제가 완료되었습니다.";
  }

  private static void updateArtistInfo(Artists artist, ArtistRequestDto dto) {
    artist.updateArtistInfo(
        Optional.ofNullable(dto.artistName()),
        Optional.ofNullable(dto.studentNo()),
        Optional.ofNullable(dto.cohort()));
  }

  private Artists findArtistById(Long artistsNo) {
    return artistsRepository
        .findById(artistsNo)
        .orElseThrow(() -> new CustomException(ArtistsException.ARTIST_NOT_FOUND));
  }

  public Artists findByStudentNo(String studentNo) {
    return artistsRepository
        .findByStudentNo(studentNo)
        .orElseThrow(() -> new CustomException(ArtistsException.ARTIST_NOT_FOUND));
  }

  private Artists saveArtist(Artists artist) {
    try {
      return artistsRepository.save(artist);
    } catch (Exception e) {
      throw new CustomException(ArtistsException.INTERNAL_SERVER_ERROR);
    }
  }

  public void isStudentNoDuplicate(String studentNo) {
    if (artistsRepository.existsByStudentNo(studentNo)) {
      throw new CustomException(ArtistsException.DUPLICATE_STUDENT_NO);
    }
  }

  private Artists convertToEntity(ArtistRequestDto dto) {
    return Artists.builder()
        .artistName(dto.artistName())
        .studentNo(dto.studentNo())
        .cohort(dto.cohort())
        .build();
  }
}
