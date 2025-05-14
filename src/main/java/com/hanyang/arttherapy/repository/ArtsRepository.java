// package com.hanyang.arttherapy.repository;
//
// import java.util.List;
// import java.util.Optional;
//
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
//
// import com.hanyang.arttherapy.domain.Arts;
//
// public interface ArtsRepository extends JpaRepository<Arts, Long> {
//
//  // 기본 조회
//  Optional<Arts> findByArtsNo(Long artsNo);
//
//  // 특정 전시회에 속한 모든 작품 조회
//  List<Arts> findByGalleries_GalleriesNo(Long galleriesNo);
//
//  @Query("SELECT ar.arts FROM ArtArtistRel ar WHERE ar.artists.studentNo = :studentNo")
//  List<Arts> findAllByStudentNo(@Param("studentNo") String studentNo);
//
//  @Query("SELECT a FROM Arts a WHERE a.galleries.user.userNo = :userNo")
//  List<Arts> findAllByUserNo(@Param("userNo") Long userNo);
// }
