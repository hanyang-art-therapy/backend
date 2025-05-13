package com.hanyang.arttherapy.service;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.GalleryExceptionType;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.dto.request.GalleriesRequestDto;
import com.hanyang.arttherapy.repository.GalleriesRepository;
import com.hanyang.arttherapy.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
@Profile("h2")
public class GalleriesService {

  private final GalleriesRepository galleriesRepository;
  private final UserRepository userRepository;

  public Galleries save(GalleriesRequestDto dto) {
    Users user =
        userRepository
            .findById(dto.getUserNo())
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    Galleries galleries =
        Galleries.builder()
            .user(user)
            .title(dto.getTitle())
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .build();

    return galleriesRepository.save(galleries);
  }

  public List<Galleries> getAllGalleries() {
    return galleriesRepository.findAll();
  }

  public Galleries getGalleryById(Long id) {
    return galleriesRepository
        .findById(id)
        .orElseThrow(() -> new CustomException(GalleryExceptionType.GALLERY_NOT_FOUND));
  }

  public Galleries update(Long id, GalleriesRequestDto dto) {
    Galleries gallery = getGalleryById(id);
    gallery.update(dto.getTitle(), dto.getStartDate(), dto.getEndDate());
    return gallery;
  }

  public void delete(Long id) {
    Galleries gallery = getGalleryById(id);
    galleriesRepository.delete(gallery);
  }
}
