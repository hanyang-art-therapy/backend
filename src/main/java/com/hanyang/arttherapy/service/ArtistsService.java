package com.hanyang.arttherapy.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.ArtistsException;
import com.hanyang.arttherapy.common.exception.exceptionType.FilteringException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.domain.Artists;
import com.hanyang.arttherapy.dto.request.ArtistRequestDto;
import com.hanyang.arttherapy.dto.response.artistResponse.ArtistResponseDto;
import com.hanyang.arttherapy.dto.response.artistResponse.ArtistScrollResponseDto;
import com.hanyang.arttherapy.repository.ArtistsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ArtistsService {

  private final ArtistsRepository artistsRepository;

  public String registerArtist(ArtistRequestDto dto) {

    // 필수 입력값 검증
    if (dto.artistName() == null || dto.artistName().isBlank()) {
      throw new CustomException(UserException.BLANK_REQUIRED);
    }

    if (dto.studentNo() == null || dto.studentNo().isBlank()) {
      throw new CustomException(UserException.BLANK_REQUIRED);
    }

    validateDuplicateStudentNo(dto.studentNo(), null);
    Artists artist = convertToEntity(dto);
    artistsRepository.save(artist);
    return "작가등록 성공";
  }

  public ArtistScrollResponseDto searchArtists(
      String filter, String keyword, Long lastId, int size) {
    List<Artists> artists;

    if ((filter == null || filter.isBlank()) && (keyword == null || keyword.isBlank())) {
      artists = artistsRepository.searchByArtistNameOrStudentNo(null, null, lastId, size);
    } else {
      if (filter == null || filter.isBlank()) {
        throw new CustomException(FilteringException.INVALID_REQUEST_FILTER);
      }
      if (keyword == null || keyword.isBlank()) {
        throw new CustomException(FilteringException.INVALID_REQUEST_KEYWORD);
      }
      artists = artistsRepository.searchByArtistNameOrStudentNo(filter, keyword, lastId, size);
    }

    List<ArtistResponseDto> dtos =
        artists.stream().map(ArtistResponseDto::of).collect(Collectors.toList());

    Long newLastNo = artists.isEmpty() ? null : artists.get(artists.size() - 1).getArtistsNo();
    boolean hasNext = artists.size() == size;

    return new ArtistScrollResponseDto(dtos, newLastNo, hasNext);
  }

  public ArtistResponseDto getArtist(Long artistNo) {
    Artists artist = findArtistById(artistNo);
    return ArtistResponseDto.of(artist);
  }

  public String updateArtist(long artistsNo, ArtistRequestDto dto) {
    Artists artist = findArtistById(artistsNo);

    if (dto.studentNo() != null && !dto.studentNo().equals(artist.getStudentNo())) {
      validateDuplicateStudentNo(dto.studentNo(), artistsNo);
    }

    artist.updateArtistInfo(
        Optional.ofNullable(dto.artistName()),
        Optional.ofNullable(dto.studentNo()),
        Optional.ofNullable(dto.cohort()));

    artistsRepository.save(artist);
    return "작가 수정이 완료되었습니다";
  }

  public String deleteArtist(Long artistsNo) {
    Artists artist = findArtistById(artistsNo);
    artistsRepository.delete(artist);
    return "작가 삭제가 완료되었습니다.";
  }

  public Artists findByStudentNo(String studentNo) {
    return artistsRepository
        .findByStudentNo(studentNo)
        .orElseThrow(() -> new CustomException(ArtistsException.ARTIST_NOT_FOUND));
  }

  private Artists findArtistById(Long artistsNo) {
    return artistsRepository
        .findById(artistsNo)
        .orElseThrow(() -> new CustomException(ArtistsException.ARTIST_NOT_FOUND));
  }

  private void validateDuplicateStudentNo(String studentNo, Long excludeArtistNo) {
    Optional<Artists> found = artistsRepository.findByStudentNo(studentNo);
    if (found.isPresent()
        && (excludeArtistNo == null || !found.get().getArtistsNo().equals(excludeArtistNo))) {
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
