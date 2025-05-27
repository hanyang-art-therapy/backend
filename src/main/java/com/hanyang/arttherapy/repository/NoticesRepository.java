package com.hanyang.arttherapy.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanyang.arttherapy.domain.Notices;
import com.hanyang.arttherapy.domain.enums.NoticeCategory;

public interface NoticesRepository extends JpaRepository<Notices, Long> {

  // 카테고리, 검색, 카테고리 + 검색
  @Query(
      "SELECT n FROM Notices n "
          + "WHERE (:category IS NULL OR n.category = :category) "
          + "AND (:keyword IS NULL OR n.title LIKE %:keyword% OR n.content LIKE %:keyword%)")
  Page<Notices> findAllBySearch(
      @Param("category") NoticeCategory category,
      @Param("keyword") String keyword,
      Pageable pageable);

  // 단건 조회
  boolean existsByNoticesNo(Long noticesNo);

  Optional<Notices> findByNoticesNo(Long noticesNo);

  // 이전글
  @Query("SELECT n FROM Notices n WHERE n.createdAt < :createdAt ORDER BY n.createdAt DESC")
  Page<Notices> findPreviousNotice(@Param("createdAt") LocalDateTime createdAt, Pageable pageable);

  // 다음글
  @Query("SELECT n FROM Notices n WHERE n.createdAt > :createdAt ORDER BY n.createdAt ASC")
  Page<Notices> findNextNotice(@Param("createdAt") LocalDateTime createdAt, Pageable pageable);
}
