package com.hanyang.arttherapy.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanyang.arttherapy.domain.Reviews;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {

  Page<Reviews> findAllByArts_ArtsNo(Long artsNo, Pageable pageable);

  @Query("SELECT r FROM Reviews r WHERE r.user.userNo = :userNo")
  List<Reviews> findAllByUserNo(@Param("userNo") Long userNo);
}
