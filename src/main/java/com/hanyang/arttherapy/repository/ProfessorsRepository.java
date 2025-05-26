package com.hanyang.arttherapy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanyang.arttherapy.domain.Professors;

public interface ProfessorsRepository extends JpaRepository<Professors, Long> {

  // 교수 이름 검색 (대소문자 무시, 한글 검색 안정화)
  @Query(
      "SELECT p FROM Professors p WHERE LOWER(p.professorName) LIKE LOWER(CONCAT('%', :name, '%'))")
  List<Professors> searchByName(@Param("name") String name);
}
