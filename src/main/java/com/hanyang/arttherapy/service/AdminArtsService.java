package com.hanyang.arttherapy.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.AdminArtsExceptionType;
import com.hanyang.arttherapy.common.exception.exceptionType.FilteringException;
import com.hanyang.arttherapy.common.filter.CustomUserDetail;
import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.dto.request.admin.AdminArtsPatchRequestDto;
import com.hanyang.arttherapy.dto.request.admin.AdminArtsRequestDto;
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
  private final FileStorageService fileStorageService;

  // 등록
  @Transactional
  public String register(AdminArtsRequestDto request, CustomUserDetail userDetail) {
    if (userDetail.getUser().getRole() != Role.ADMIN) {
      throw new CustomException(AdminArtsExceptionType.UNAUTHORIZED);
    }

    Files file =
        filesRepository
            .findById(request.getFilesNo())
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.FILE_NOT_FOUND));

    file.activateFile();
    filesRepository.save(file);

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

  // 수정
  @Transactional
  public String update(Long artsNo, AdminArtsPatchRequestDto request, CustomUserDetail userDetail) {
    if (userDetail.getUser().getRole() != Role.ADMIN) {
      throw new CustomException(AdminArtsExceptionType.UNAUTHORIZED);
    }

    Arts art =
        artsRepository
            .findById(artsNo)
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTS_NOT_FOUND));

    // 파일 soft delete → 새 파일 교체
    if (request.getFilesNo() != null
        && art.getFile() != null
        && !art.getFile().getFilesNo().equals(request.getFilesNo())) {
      fileStorageService.softDeleteFile(art.getFile().getFilesNo());
    }

    if (request.getFilesNo() != null) {
      Files file =
          filesRepository
              .findById(request.getFilesNo())
              .orElseThrow(() -> new CustomException(AdminArtsExceptionType.FILE_NOT_FOUND));
      file.activateFile();
      filesRepository.save(file);
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

    //  작가 추가/수정/삭제 처리
    if (request.getArtistList() != null) {
      // 1. 현재 관계 조회
      List<ArtArtistRel> existingRels = artArtistRelRepository.findByArts(art);

      // 2. 요청 받은 artistNo 목록으로 매핑
      List<Long> requestedArtistNos =
          request.getArtistList().stream()
              .map(AdminArtsPatchRequestDto.ArtistInfo::getArtistNo)
              .toList();

      // 3. 기존 관계 중 요청에 없는 작가 삭제
      for (ArtArtistRel rel : existingRels) {
        if (!requestedArtistNos.contains(rel.getArtists().getArtistNo())) {
          artArtistRelRepository.delete(rel);
        }
      }

      // 4. 요청 목록 처리
      for (AdminArtsPatchRequestDto.ArtistInfo info : request.getArtistList()) {
        Long artistNo = info.getArtistNo();
        String newDesc = info.getDescription();

        // 이미 존재하는 경우 → 설명 업데이트
        ArtArtistRel existing =
            existingRels.stream()
                .filter(r -> r.getArtists().getArtistNo().equals(artistNo))
                .findFirst()
                .orElse(null);

        if (existing != null) {
          existing.updateDescription(newDesc);
        } else {
          // 없는 경우 → 새로 추가
          Artists artist =
              artistsRepository
                  .findById(artistNo)
                  .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTIST_NOT_FOUND));
          ArtArtistRel newRel =
              ArtArtistRel.builder().arts(art).artists(artist).description(newDesc).build();
          artArtistRelRepository.save(newRel);
        }
      }
    }

    return "작품 수정에 성공했습니다";
  }

  // 삭제
  @Transactional
  public String delete(Long artsNo, CustomUserDetail userDetail) {
    if (userDetail.getUser().getRole() != Role.ADMIN) {
      throw new CustomException(AdminArtsExceptionType.UNAUTHORIZED);
    }

    Arts art =
        artsRepository
            .findById(artsNo)
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTS_NOT_FOUND));

    if (art.getFile() != null) {
      Files file =
          filesRepository
              .findById(art.getFile().getFilesNo())
              .orElseThrow(() -> new CustomException(AdminArtsExceptionType.FILE_NOT_FOUND));
      file.markAsDeleted();
      filesRepository.save(file);
    }

    artArtistRelRepository.deleteByArts(art);
    artsRepository.delete(art);

    return "작품 삭제에 성공했습니다";
  }

  // 무한 스크롤 기반 전체 조회 or 검색 조회
  @Transactional(readOnly = true)
  public CommonScrollResponse<AdminArtsListResponseDto> getArtsWithScroll(
      String filter, String keyword, Long lastId, int size) {
    Pageable pageable = PageRequest.of(0, size);
    List<Arts> arts;

    if ((filter == null || filter.isBlank()) && (keyword == null || keyword.isBlank())) {
      arts = artsRepository.findAllArtsWithCursor(lastId, pageable);
    } else {
      if (filter == null || filter.isBlank()) {
        throw new CustomException(FilteringException.INVALID_REQUEST_FILTER);
      }
      if (keyword == null || keyword.isBlank()) {
        throw new CustomException(FilteringException.INVALID_REQUEST_KEYWORD);
      }
      arts = artsRepository.searchArtsWithCursor(keyword, lastId, pageable);
    }

    List<AdminArtsListResponseDto> content = arts.stream().map(this::toListDto).toList();

    Long nextCursor = content.isEmpty() ? null : content.get(content.size() - 1).getArtsNo();
    boolean hasNext = arts.size() == size;

    return new CommonScrollResponse<>(content, nextCursor, hasNext);
  }

  // 삭제
  @Transactional(readOnly = true)
  public AdminArtsDetailResponseDto getArtDetail(Long artsNo) {
    Arts art =
        artsRepository
            .findById(artsNo)
            .orElseThrow(() -> new CustomException(AdminArtsExceptionType.ARTS_NOT_FOUND));

    Galleries gallery = art.getGalleries();
    if (gallery == null) {
      throw new CustomException(AdminArtsExceptionType.GALLERY_NOT_FOUND);
    }

    String fileUrl = null;
    if (art.getFile() != null) {
      fileUrl = fileStorageService.getFileUrl(art.getFile().getFilesNo());
    }

    List<AdminArtsDetailResponseDto.ArtistInfo> artistInfos =
        art.getArtArtistRels().stream()
            .map(
                rel ->
                    AdminArtsDetailResponseDto.ArtistInfo.builder()
                        .artistNo(rel.getArtists().getArtistNo())
                        .name(rel.getArtists().getArtistName())
                        .description(rel.getDescription())
                        .build())
            .toList();

    return AdminArtsDetailResponseDto.builder()
        .artsNo(art.getArtsNo())
        .artName(art.getArtName())
        .caption(art.getCaption())
        .artType(art.getArtType().name())
        .fileUrl(fileUrl)
        .galleriesNo(gallery.getGalleriesNo())
        .title(gallery.getTitle())
        .coDescription(art.getCoDescription())
        .artists(artistInfos)
        .build();
  }

  // 전체 조회
  private AdminArtsListResponseDto toListDto(Arts art) {
    Galleries gallery = art.getGalleries();
    if (gallery == null) {
      throw new CustomException(AdminArtsExceptionType.GALLERY_NOT_FOUND);
    }

    List<String> artistNames =
        art.getArtArtistRels().stream().map(rel -> rel.getArtists().getArtistName()).toList();

    return AdminArtsListResponseDto.builder()
        .artsNo(art.getArtsNo())
        .artName(art.getArtName())
        .galleriesNo(gallery.getGalleriesNo())
        .galleriesTitle(gallery.getTitle())
        .artists(artistNames)
        .build();
  }
}
