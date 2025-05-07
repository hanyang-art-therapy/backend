package com.hanyang.arttherapy.repository;

import java.time.*;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanyang.arttherapy.domain.*;

public interface FilesRepository extends JpaRepository<Files, Long> {
  Optional<Files> findByFilesNoAndUseYn(Long id, boolean userYn);

  List<Files> findByCreatedAtBeforeAndDelYn(LocalDateTime cutoffDate, boolean delYn);

  List<Files> findByCreatedAtBeforeAndUseYn(LocalDateTime cutoffDate, boolean useYn);
}
