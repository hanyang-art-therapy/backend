package com.hanyang.arttherapy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanyang.arttherapy.domain.Arts;

public interface ArtsRepository extends JpaRepository<Arts, Long> {

  // 엔티티 조회
  Optional<Arts> findByArtsNo(Long artsNo);

  // 특정 전시회에 속한 작품 조회
  List<Arts> findByGalleriesNo(Long galleriesNo);

  // 기수로 조회
  @Query(
      """
        SELECT a FROM Arts a
        JOIN ArtArtistRel ar ON a.artsNo = ar.artsNo
        JOIN Artists artist ON ar.artistsNo = artist.artistsNo
        WHERE artist.cohort = :cohort
    """)
  List<Arts> findByCohort(@Param("cohort") int cohort);

  // 특정 전시회 + 기수(Cohort)로 작품 조회
  @Query(
      """
        SELECT a FROM Arts a
        JOIN ArtArtistRel ar ON a.artsNo = ar.artsNo
        JOIN Artists artist ON ar.artistsNo = artist.artistsNo
        WHERE a.galleriesNo = :galleriesNo
        AND artist.cohort = :cohort
    """)
  List<Arts> findByGalleriesNoAndCohort(
      @Param("galleriesNo") Long galleriesNo, @Param("cohort") int cohort);

  // 작품명 검색
  List<Arts> findByArtNameContaining(String keyword);

  //  @Query("SELECT a FROM Arts a WHERE a.galleries.user.userNo = :userNo")
  //  List<Arts> findAllByUserNo(@Param("userNo") Long userNo);
}
