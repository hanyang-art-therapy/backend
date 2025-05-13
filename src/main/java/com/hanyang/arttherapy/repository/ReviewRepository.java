package com.hanyang.arttherapy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hanyang.arttherapy.domain.Reviews;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {

  Page<Reviews> findAllByArtsNo(Long artsNo, Pageable pageable);

  //  @Query("SELECT r FROM Reviews r WHERE r.user.userNo = :userNo")
  //  List<Reviews> findAllByUser_UserNo(@Param("userNo") Long userNo);
}
