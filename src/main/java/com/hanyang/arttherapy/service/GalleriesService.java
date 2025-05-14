// package com.hanyang.arttherapy.service;
//
// import java.util.List;
// import java.util.stream.Collectors;
//
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.hanyang.arttherapy.common.exception.CustomException;
// import com.hanyang.arttherapy.common.exception.exceptionType.GalleryExceptionType;
// import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
// import com.hanyang.arttherapy.domain.Galleries;
// import com.hanyang.arttherapy.domain.Users;
// import com.hanyang.arttherapy.dto.request.GalleriesRequestDto;
// import com.hanyang.arttherapy.dto.response.GalleriesResponseDto;
// import com.hanyang.arttherapy.repository.GalleriesRepository;
// import com.hanyang.arttherapy.repository.UserRepository;
//
// import lombok.RequiredArgsConstructor;
//
// @Service
// @RequiredArgsConstructor
// @Transactional
// public class GalleriesService {
//
//  private final GalleriesRepository galleriesRepository;
//  private final UserRepository userRepository;
//
//  public GalleriesResponseDto save(GalleriesRequestDto dto, Long userId) {
//    Users user =
//        userRepository
//            .findById(userId)
//            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));
//
//    Galleries gallery =
//        Galleries.builder()
//            .title(dto.getTitle())
//            .startDate(dto.getStartDate())
//            .endDate(dto.getEndDate())
//            .user(user)
//            .build();
//
//    Galleries saved = galleriesRepository.save(gallery);
//    return GalleriesResponseDto.from(saved);
//  }
//
//  public List<GalleriesResponseDto> getAllGalleries() {
//    return galleriesRepository.findAll().stream()
//        .map(GalleriesResponseDto::from)
//        .collect(Collectors.toList());
//  }
//
//  public GalleriesResponseDto getGalleryById(Long id) {
//    Galleries gallery =
//        galleriesRepository
//            .findById(id)
//            .orElseThrow(() -> new CustomException(GalleryExceptionType.GALLERY_NOT_FOUND));
//    return GalleriesResponseDto.from(gallery);
//  }
//
//  public GalleriesResponseDto update(Long id, GalleriesRequestDto dto) {
//    Galleries gallery =
//        galleriesRepository
//            .findById(id)
//            .orElseThrow(() -> new CustomException(GalleryExceptionType.GALLERY_NOT_FOUND));
//    gallery.update(dto.getTitle(), dto.getStartDate(), dto.getEndDate());
//    return GalleriesResponseDto.from(gallery);
//  }
//
//  public void delete(Long id) {
//    Galleries gallery =
//        galleriesRepository
//            .findById(id)
//            .orElseThrow(() -> new CustomException(GalleryExceptionType.GALLERY_NOT_FOUND));
//    galleriesRepository.delete(gallery);
//  }
// }
