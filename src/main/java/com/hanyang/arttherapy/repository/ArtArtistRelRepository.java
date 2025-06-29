package com.hanyang.arttherapy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanyang.arttherapy.domain.ArtArtistRel;
import com.hanyang.arttherapy.domain.Arts;

public interface ArtArtistRelRepository extends JpaRepository<ArtArtistRel, Long> {

  // 작품 번호로 아티스트 작품 관계 조회
  List<ArtArtistRel> findByArts_ArtsNo(Long artsNo);

  @Query(
      """
      SELECT ar
      FROM ArtArtistRel ar
      JOIN FETCH ar.artists
      WHERE ar.arts.artsNo IN :artsNos
    """)
  List<ArtArtistRel> findWithArtistsByArtsNoIn(@Param("artsNos") List<Long> artsNos);

  // 관리자 작품
  void deleteByArts(Arts arts);

  // 기수 목록 조회
  @Query(
      """
    SELECT DISTINCT r.artists.cohort
    FROM ArtArtistRel r
    JOIN r.arts a
    JOIN a.galleries g
    WHERE FUNCTION('YEAR', g.startDate) = :year
    ORDER BY r.artists.cohort ASC
""")
  List<Integer> findDistinctCohortsByYear(@Param("year") int year);

  List<ArtArtistRel> findByArtists_ArtistNo(Long artistNo);

  List<ArtArtistRel> findByArts(Arts arts);
}
