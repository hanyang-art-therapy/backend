package com.hanyang.arttherapy.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanyang.arttherapy.domain.Notices;

public interface NoticesRepository extends JpaRepository<Notices, Long> {

  // 제목 또는 내용에 keyword가 포함된 공지 검색
  @Query(
      "SELECT n FROM Notices n "
          + "WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "ORDER BY n.isFixed DESC, n.createdAt DESC")
  Page<Notices> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

  Optional<Notices> findByNoticesNo(Long noticesNo);

  // 이전글
  @Query("SELECT n FROM Notices n WHERE n.createdAt < :createdAt ORDER BY n.createdAt DESC")
  Page<Notices> findPreviousNotice(@Param("createdAt") LocalDateTime createdAt, Pageable pageable);

  // 다음글
  @Query("SELECT n FROM Notices n WHERE n.createdAt > :createdAt ORDER BY n.createdAt ASC")
  Page<Notices> findNextNotice(@Param("createdAt") LocalDateTime createdAt, Pageable pageable);
}
