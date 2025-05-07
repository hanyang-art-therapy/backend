package com.hanyang.arttherapy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.repository.GalleriesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GalleriesService {

  private final GalleriesRepository galleriesRepository;

  // 전시회 전체 조회
  public List<Galleries> getAllGalleries() {
    return galleriesRepository.findAll();
  }

  // 전시회 상세 조회
  public Galleries getGalleryById(Long id) {
    return galleriesRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 전시회가 존재하지 않습니다."));
  }
}
