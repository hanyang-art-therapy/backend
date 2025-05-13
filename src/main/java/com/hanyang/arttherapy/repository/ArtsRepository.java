package com.hanyang.arttherapy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hanyang.arttherapy.domain.Arts;

@Repository
public interface ArtsRepository extends JpaRepository<Arts, Long> {

  @Query("SELECT a FROM Arts a WHERE a.artsNo = :artsNo")
  Optional<Arts> findArtDetailById(@Param("artsNo") Long artsNo);

  @Query(value = "SELECT a.filesNo FROM arts a WHERE a.artsNo = :artsNo", nativeQuery = true)
  List<Long> findFileIdsByArtsNo(@Param("artsNo") Long artsNo);

  @Query("SELECT ar.arts FROM ArtArtistRel ar WHERE ar.artist.studentNo = :studentNo")
  List<Arts> findAllByStudentNo(@Param("studentNo") String studentNo);
}
