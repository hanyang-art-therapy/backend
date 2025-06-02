package com.hanyang.arttherapy.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.GalleryExceptionType;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.common.filter.CustomUserDetail;
import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.dto.request.GalleriesRequestDto;
import com.hanyang.arttherapy.dto.response.GalleriesResponseDto;
import com.hanyang.arttherapy.repository.GalleriesRepository;
import com.hanyang.arttherapy.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GalleriesService {

  private final GalleriesRepository galleriesRepository;
  private final UserRepository userRepository;

  public String save(GalleriesRequestDto dto, CustomUserDetail userDetail) {
    Users user =
        userRepository
            .findById(userDetail.getUser().getUserNo())
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    if (user.getRole() != Role.ADMIN) {
      throw new CustomException(GalleryExceptionType.UNAUTHORIZED);
    }

    try {
      Galleries gallery =
          Galleries.builder()
              .title(dto.getTitle())
              .startDate(dto.getStartDate())
              .endDate(dto.getEndDate())
              .user(user)
              .build();

      galleriesRepository.save(gallery);
      return "전시회 등록이 완료되었습니다.";

    } catch (Exception e) {
      log.error("전시회 등록 실패: {}", e.getMessage());
      throw new CustomException(GalleryExceptionType.GALLERY_CREATE_FAIL);
    }
  }

  @Transactional(readOnly = true)
  public List<GalleriesResponseDto> getAllGalleries() {
    return galleriesRepository.findAll().stream()
        .map(GalleriesResponseDto::from)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public GalleriesResponseDto getGalleryById(Long id) {
    Galleries gallery =
        galleriesRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(GalleryExceptionType.GALLERY_NOT_FOUND));
    return GalleriesResponseDto.from(gallery);
  }

  public String update(Long id, GalleriesRequestDto dto, CustomUserDetail userDetail) {
    if (userDetail.getUser().getRole() != Role.ADMIN) {
      throw new CustomException(GalleryExceptionType.UNAUTHORIZED);
    }

    Galleries gallery =
        galleriesRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(GalleryExceptionType.GALLERY_NOT_FOUND));

    try {
      gallery.update(
          dto.getTitle() != null ? dto.getTitle() : gallery.getTitle(),
          dto.getStartDate() != null ? dto.getStartDate() : gallery.getStartDate(),
          dto.getEndDate() != null ? dto.getEndDate() : gallery.getEndDate());
      return "전시회 정보수정이 완료되었습니다.";

    } catch (Exception e) {
      log.error("전시회 수정 실패: {}", e.getMessage());
      throw new CustomException(GalleryExceptionType.GALLERY_UPDATE_FAIL);
    }
  }

  public String delete(Long id, CustomUserDetail userDetail) {
    if (userDetail.getUser().getRole() != Role.ADMIN) {
      throw new CustomException(GalleryExceptionType.UNAUTHORIZED);
    }

    Galleries gallery =
        galleriesRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(GalleryExceptionType.GALLERY_NOT_FOUND));

    try {
      galleriesRepository.delete(gallery);
      return "전시회 삭제가 완료되었습니다.";

    } catch (Exception e) {
      log.error("전시회 삭제 실패: {}", e.getMessage());
      throw new CustomException(GalleryExceptionType.GALLERY_DELETE_FAIL);
    }
  }
}
