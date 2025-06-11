package com.hanyang.arttherapy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanyang.arttherapy.domain.Galleries;

@Repository
public interface GalleriesRepository extends JpaRepository<Galleries, Long> {

  // 특정 기간에 열린 전시회 조회
  List<Galleries> findByStartDateBetween(LocalDate start, LocalDate end);

  boolean existsByStartDateBetween(LocalDate start, LocalDate end);
}
