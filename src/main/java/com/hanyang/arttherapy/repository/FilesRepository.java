package com.hanyang.arttherapy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanyang.arttherapy.domain.Files;

public interface FilesRepository extends JpaRepository<Files, Long> {}
