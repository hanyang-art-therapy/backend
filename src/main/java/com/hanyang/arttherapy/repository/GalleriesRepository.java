package com.hanyang.arttherapy.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanyang.arttherapy.domain.Galleries;

@Repository
public interface GalleriesRepository extends JpaRepository<Galleries, Long> {
  List<Galleries> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
}
