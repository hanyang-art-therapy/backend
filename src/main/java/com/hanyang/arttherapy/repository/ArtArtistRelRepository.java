package com.hanyang.arttherapy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanyang.arttherapy.domain.ArtArtistRel;
import com.hanyang.arttherapy.domain.Arts;

public interface ArtArtistRelRepository extends JpaRepository<ArtArtistRel, Long> {
  void deleteAllByArts(Arts arts);
}
