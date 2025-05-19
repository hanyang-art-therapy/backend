package com.hanyang.arttherapy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanyang.arttherapy.domain.ArtArtistRel;
import com.hanyang.arttherapy.domain.Arts;

public interface ArtArtistRelRepository extends JpaRepository<ArtArtistRel, Long> {

  // 작품 번호로 아티스트 작품 관계 조회
  List<ArtArtistRel> findByArts_ArtsNo(Long artsNo);

  void deleteAllByArts(Arts arts);

  // 관리자 작품
  void deleteByArts(Arts arts);
}
