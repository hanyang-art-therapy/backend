package com.hanyang.arttherapy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanyang.arttherapy.domain.Artists;

public interface ArtistsRepository extends JpaRepository<Artists, Long> {
  boolean existsByStudentNo(String studentNo);
}
