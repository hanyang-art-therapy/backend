package com.hanyang.arttherapy.service;

import org.springframework.stereotype.Service;

import com.hanyang.arttherapy.domain.Galleries;
import com.hanyang.arttherapy.repository.GalleriesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GalleriesService {

  private final GalleriesRepository galleriesRepository;

  // 전시회 등록
  public Galleries save(Galleries galleries) {
    return galleriesRepository.save(galleries);
  }
}
