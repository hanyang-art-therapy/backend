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
      String filter, String keyword, Long lastId, int size) {
    QArtists artist = QArtists.artists;

    BooleanBuilder builder = new BooleanBuilder(); // 조건을 동적으로 추가할 BooleanBuilder 준비

    // filter와 keyword가 모두 제공되었는지 확인하는 변수
    boolean isFilteredSearch =
        (filter != null && !filter.isBlank()) && (keyword != null && !keyword.isBlank());

    if (isFilteredSearch) {
      // filter와 keyword가 있을 때만! 필터링 조건을 builder에 추가해요.
      if ("artistName".equals(filter)) {
        builder.and(artist.artistName.containsIgnoreCase(keyword.trim()));
      } else if ("studentNo".equals(filter)) {
        builder.and(artist.studentNo.equalsIgnoreCase(keyword.trim())); // 정확 일치
      } else {
        // filter와 keyword는 왔는데 filter 값이 유효하지 않으면 예외 발생!
        // (이 경우는 서비스 레이어에서 이미 체크하지만, 여기서 한 번 더 체크해도 좋아요!)
        throw new CustomException(FilteringException.INVALID_REQUEST_FILTER);
      }
    }

    if (lastId != null) {
      builder.and(artist.artistsNo.lt(lastId));
    }

    return queryFactory
        .selectFrom(artist)
        .where(builder)
        .orderBy(artist.artistsNo.desc())
        .limit(size)
        .fetch();
  }
}
