package com.hanyang.arttherapy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanyang.arttherapy.domain.Reviews;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {

  Page<Reviews> findAllByArts_ArtsNo(Long artsNo, Pageable pageable);

  @Query(
      "SELECT r FROM Reviews r JOIN r.arts a "
          + "WHERE r.user.userNo = :userNo "
          + "AND (:keyword IS NULL OR a.artName LIKE %:keyword% OR r.reviewText LIKE %:keyword%)")
  Page<Reviews> findByUserAndKeyword(
      @Param("userNo") Long userNo, @Param("keyword") String keyword, Pageable pageable);
}
