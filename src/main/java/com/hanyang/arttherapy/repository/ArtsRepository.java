package com.hanyang.arttherapy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanyang.arttherapy.domain.Arts;

public interface ArtsRepository extends JpaRepository<Arts, Long> {

  // 기본 조회
  Optional<Arts> findByArtsNo(Long artsNo);

  // 특정 전시회에 속한 모든 작품 조회
  List<Arts> findTop9ByGalleries_GalleriesNoAndArtsNoGreaterThanOrderByArtsNoAsc(
      Long galleriesNo, Long lastId);

  // 기수별 조회를 무한 스크롤로 지원
  List<Arts> findTop9ByArtArtistRels_Artists_CohortAndArtsNoGreaterThanOrderByArtsNoAsc(
      int cohort, Long lastId);

  List<Arts>
      findTop9ByGalleries_GalleriesNoAndArtArtistRels_Artists_CohortAndArtsNoGreaterThanOrderByArtsNoAsc(
          Long galleriesNo, int cohort, Long artsNo);

  Long countByGalleries_GalleriesNo(Long galleriesNo);

  Long countByGalleries_GalleriesNoAndArtArtistRels_Artists_Cohort(Long galleriesNo, int cohort);

  Long countByArtArtistRels_Artists_Cohort(int cohort);

  @Query(
      value =
          """
    SELECT a.* FROM arts a
    JOIN art_artist_rel ar ON a.arts_no = ar.arts_no
    JOIN artists artist ON ar.artists_no = artist.artists_no
    WHERE artist.student_no = :studentNo
""",
      nativeQuery = true)
  List<Arts> findAllByStudentNo(@Param("studentNo") String studentNo);
}
