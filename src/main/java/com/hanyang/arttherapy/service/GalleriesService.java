package com.hanyang.arttherapy.service;

import org.springframework.stereotype.Service;

import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.repository.GalleriesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GalleriesService {

  private final GalleriesRepository galleriesRepository;

  // 전시회 수정
  public Galleries update(Long id, Galleries updated) {
    Galleries gallery = getGalleryById(id);
    gallery.update(updated.getTitle(), updated.getStartDate(), updated.getEndDate());
    return galleriesRepository.save(gallery);
  }
}
