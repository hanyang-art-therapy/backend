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

  public Galleries save(Galleries galleries) {
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

  public Galleries update(Long id, Galleries updated) {
    Galleries gallery = getGalleryById(id);
    gallery.update(updated.getTitle(), updated.getStartDate(), updated.getEndDate());
    return galleriesRepository.save(gallery);
  }

  public void delete(Long id) {
    Galleries gallery = getGalleryById(id);
    galleriesRepository.delete(gallery);
  }
}
