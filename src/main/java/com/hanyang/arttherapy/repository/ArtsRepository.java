package com.hanyang.arttherapy.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanyang.arttherapy.domain.Arts;

public interface ArtsRepository extends JpaRepository<Arts, Long> {

  // 특정 전시회에 속한 모든 작품 조회
  List<Arts> findTop9ByGalleries_GalleriesNoAndArtsNoGreaterThanOrderByArtsNoAsc(
      Long galleriesNo, Long lastId);

  // 기수별 조회를 무한 스크롤로 지원
  List<Arts> findTop9ByArtArtistRels_Artists_CohortAndArtsNoGreaterThanOrderByArtsNoAsc(
      int cohort, Long lastId);

  List<Arts>
      findTop9ByGalleries_GalleriesNoAndArtArtistRels_Artists_CohortAndArtsNoGreaterThanOrderByArtsNoAsc(
          Long galleriesNo, int cohort, Long artsNo);

  // 마이페이지 나의 게시글
  @Query(
      value =
          """
                      SELECT a.* FROM arts a
                      JOIN art_artist_rel ar ON a.arts_no = ar.arts_no
                      JOIN artists artist ON ar.artist_no = artist.artist_no
                      WHERE artist.student_no = :studentNo
                      """,
      nativeQuery = true)
  List<Arts> findAllByStudentNo(@Param("studentNo") String studentNo);

  // 검색 기능(작품+작가)
  @Query(
      """
          SELECT DISTINCT a FROM Arts a
          JOIN a.artArtistRels r
          JOIN r.artists artist
          WHERE a.artName LIKE CONCAT('%', :keyword, '%') OR artist.artistName LIKE CONCAT('%', :keyword, '%')
          """)
  List<Arts> findByArtNameOrArtistNameContaining(@Param("keyword") String keyword);

  // 무한스크롤 + 키워드 검색 (작품명 or 작가명)
  @Query(
      """
          SELECT DISTINCT a FROM Arts a
          JOIN a.artArtistRels r
          JOIN r.artists artist
          WHERE (:keyword IS NULL OR a.artName LIKE CONCAT('%', :keyword, '%') OR artist.artistName LIKE CONCAT('%', :keyword, '%'))
            AND (:lastId IS NULL OR a.artsNo < :lastId)
          ORDER BY a.artsNo DESC
          """)
  List<Arts> searchArtsWithCursor(
      @Param("keyword") String keyword, @Param("lastId") Long lastId, Pageable pageable);

  // 무한스크롤 전체 조회용
  @Query(
      """
  SELECT a FROM Arts a
  WHERE (:lastId IS NULL OR a.artsNo < :lastId)
  ORDER BY a.artsNo DESC
""")
  List<Arts> findAllArtsWithCursor(@Param("lastId") Long lastId, Pageable pageable);
}
