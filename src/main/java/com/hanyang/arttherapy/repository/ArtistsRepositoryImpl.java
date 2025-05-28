package com.hanyang.arttherapy.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.FilteringException;
import com.hanyang.arttherapy.domain.Artists;
import com.hanyang.arttherapy.domain.QArtists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArtistsRepositoryImpl implements ArtistRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Artists> searchByArtistNameOrStudentNo(
      String filter, String keyword, Long lastNo, int size) {
    QArtists artist = QArtists.artists;

    BooleanBuilder builder = new BooleanBuilder();

    if ("artistName".equals(filter)) {
      builder.and(artist.artistName.containsIgnoreCase(keyword.trim()));
    } else if ("studentNo".equals(filter)) {
      builder.and(artist.studentNo.equalsIgnoreCase(keyword.trim())); // 정확 일치
    } else {
      throw new CustomException(FilteringException.INVALID_REQUEST_FILTER);
    }

    if (lastNo != null) {
      builder.and(artist.artistsNo.lt(lastNo));
    }

    return queryFactory
        .selectFrom(artist)
        .where(builder)
        .orderBy(artist.artistsNo.desc())
        .limit(size)
        .fetch();
  }
}
