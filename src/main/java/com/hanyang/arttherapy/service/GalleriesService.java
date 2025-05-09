package com.hanyang.arttherapy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.GalleryExceptionType;
import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.repository.GalleriesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GalleriesService {

  private final GalleriesRepository galleriesRepository;

  // 전시회 등록
  public Galleries save(Galleries galleries) {
    return galleriesRepository.save(galleries);
  }

  // 전시회 전체 조회
  public List<Galleries> getAllGalleries() {
    return galleriesRepository.findAll();
  }

  // 전시회 상세 조회
  public Galleries getGalleryById(Long id) {
    return galleriesRepository
        .findById(id)
        .orElseThrow(() -> new CustomException(GalleryExceptionType.GALLERY_NOT_FOUND));
  }

  // 전시회 수정
  public Galleries update(Long id, Galleries updated) {
    Galleries gallery = getGalleryById(id);
    gallery.update(updated.getTitle(), updated.getStartDate(), updated.getEndDate());
    return galleriesRepository.save(gallery);
  }

  // 전시회 삭제
  public void delete(Long id) {
    Galleries gallery = getGalleryById(id);
    galleriesRepository.delete(gallery);
  }
}
