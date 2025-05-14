package com.hanyang.arttherapy.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanyang.arttherapy.domain.Galleries;

@Repository
public interface GalleriesRepository extends JpaRepository<Galleries, Long> {

  // 특정 기간에 열린 전시회 조회
  List<Galleries> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

  // 특정 사용자(User) 기준 전시회 조회
  List<Galleries> findByUser_UserNo(Long userNo);

  // 전시회 제목으로 검색
  List<Galleries> findByTitleContaining(String title);
}
