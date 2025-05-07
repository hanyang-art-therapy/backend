package com.hanyang.arttherapy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanyang.arttherapy.domain.Artists;

public interface ArtistsRepository extends JpaRepository<Artists, Long> {
  boolean existsByStudentNo(String studentNo);

  Optional<Artists> findByStudentNo(String studentNo);
}
