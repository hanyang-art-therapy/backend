package com.hanyang.arttherapy.repository;

import java.util.List;

import com.hanyang.arttherapy.domain.Artists;

public interface ArtistRepositoryCustom {
  List<Artists> searchByArtistNameOrStudentNo(String filter, String keyword, Long lastNo, int size);
}
