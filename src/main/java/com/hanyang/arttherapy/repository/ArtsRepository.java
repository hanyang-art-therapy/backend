package com.hanyang.arttherapy.repository;

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
}
