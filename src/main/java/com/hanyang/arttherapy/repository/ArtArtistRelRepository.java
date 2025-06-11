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

  @Query(
      """
  SELECT DISTINCT r.artists.cohort
  FROM ArtArtistRel r
  WHERE r.arts.galleries.galleriesNo = :galleriesNo
  ORDER BY r.artists.cohort ASC
""")
  List<Integer> findDistinctCohortsByGalleriesNo(@Param("galleriesNo") Long galleriesNo);

  List<ArtArtistRel> findByArtists_ArtistNo(Long artistNo);
}
