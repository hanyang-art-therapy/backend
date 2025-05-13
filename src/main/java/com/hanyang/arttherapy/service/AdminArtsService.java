package com.hanyang.arttherapy.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.AdminArtsExceptionType;
import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.dto.request.AdminArtsRequestDto;
import com.hanyang.arttherapy.dto.response.AdminArtsResponseDto;
import com.hanyang.arttherapy.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminArtsService {

  private final ArtsRepository artsRepository;
  private final GalleriesRepository galleriesRepository;
  private final FilesRepository filesRepository;
  private final ArtistsRepository artistsRepository;
  private final ArtArtistRelRepository artArtistRelRepository;

  public AdminArtsResponseDto save(AdminArtsRequestDto dto) {
    // 갤러리/파일 존재 여부만 체크 (엔티티 저장 안 함)
    if (!galleriesRepository.existsById(dto.getGalleriesNo())) {
      throw new CustomException(AdminArtsExceptionType.GALLERY_NOT_FOUND);
    }

    if (!filesRepository.existsById(dto.getFilesNo())) {
      throw new CustomException(AdminArtsExceptionType.FILE_NOT_FOUND);
    }

    Arts arts =
        Arts.builder()
            .galleriesNo(dto.getGalleriesNo())
            .filesNo(dto.getFilesNo())
            .artName(dto.getArtName())
            .caption(dto.getCaption())
            .artType(dto.getArtType())
            .build();

    Arts saved = artsRepository.save(arts);

    if (dto.getArtistList() == null) {
      throw new CustomException(AdminArtsExceptionType.ARTIST_LIST_NULL);
    }

    dto.getArtistList()
        .forEach(
            artistDto -> {
              Artists artist =
                  artistsRepository
                      .findById(artistDto.getArtistId())
                      .orElseThrow(
                          () -> new CustomException(AdminArtsExceptionType.ARTIST_NOT_FOUND));

              ArtArtistRel rel =
                  ArtArtistRel.builder()
                      .arts(saved)
                      .artist(artist)
                      .description(artistDto.getDescription())
                      .build();

              artArtistRelRepository.save(rel);
            });

    return AdminArtsResponseDto.from(saved);
  }

  public AdminArtsResponseDto update(Long id, AdminArtsRequestDto dto) {
    Arts arts =
        artsRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTS_NOT_FOUND));

    if (!filesRepository.existsById(dto.getFilesNo())) {
      throw new CustomException(AdminArtsExceptionType.FILE_NOT_FOUND);
    }

    artArtistRelRepository.deleteAllByArts(arts);

    if (dto.getArtistList() == null) {
      throw new CustomException(AdminArtsExceptionType.ARTIST_LIST_NULL);
    }

    dto.getArtistList()
        .forEach(
            artistDto -> {
              Artists artist =
                  artistsRepository
                      .findById(artistDto.getArtistId())
                      .orElseThrow(
                          () -> new CustomException(AdminArtsExceptionType.ARTIST_NOT_FOUND));

              ArtArtistRel rel =
                  ArtArtistRel.builder()
                      .arts(arts)
                      .artist(artist)
                      .description(artistDto.getDescription())
                      .build();

              artArtistRelRepository.save(rel);
            });

    arts.updateArts(dto.getFilesNo(), dto.getArtName(), dto.getCaption(), dto.getArtType());

    return AdminArtsResponseDto.from(arts);
  }

  public void delete(Long id) {
    Arts arts =
        artsRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTS_NOT_FOUND));
    artsRepository.delete(arts);
  }

  public List<AdminArtsResponseDto> findAll() {
    return artsRepository.findAll().stream()
        .map(AdminArtsResponseDto::from)
        .collect(Collectors.toList());
  }

  public AdminArtsResponseDto findById(Long id) {
    Arts arts =
        artsRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTS_NOT_FOUND));
    return AdminArtsResponseDto.from(arts);
  }
}
