package com.hanyang.arttherapy.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hanyang.arttherapy.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  @Query("SELECT r FROM Review r WHERE r.arts.artsNo = :artsNo")
  Page<Review> findAllByArtsNo(@Param("artsNo") Long artsNo, Pageable pageable);

  @Query(value = "SELECT r.filesNo FROM reviews r WHERE r.reviewNo = :reviewNo", nativeQuery = true)
  List<Long> findFileIdsByReviewNo(@Param("reviewNo") Long reviewNo);

  @Query("SELECT r FROM Review r WHERE r.user.userNo = :userNo")
  List<Review> findAllByUser_UserNo(@Param("userNo") Long userNo);
}
