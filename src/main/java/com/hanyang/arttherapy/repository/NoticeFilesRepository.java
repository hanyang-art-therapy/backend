package com.hanyang.arttherapy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanyang.arttherapy.domain.NoticeFiles;
import com.hanyang.arttherapy.domain.Notices;

public interface NoticeFilesRepository extends JpaRepository<NoticeFiles, Long> {

  // 특정 공지에 연결된 파일 존재 여부
  boolean existsByNotice(Notices notice);

  // 첨부파일 목록 조회
  List<NoticeFiles> findAllByNotice(Notices notice);
}
