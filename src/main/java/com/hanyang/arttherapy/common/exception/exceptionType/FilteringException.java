package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum FilteringException implements ExceptionType {
  INVALID_REQUEST_FILTER(HttpStatus.BAD_REQUEST, "검색 조건을 입력해주세요."),
  INVALID_REQUEST_KEYWORD(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요."),
  NO_SEARCH_RESULT(HttpStatus.NOT_FOUND, "검색 결과가 없습니다.");

  private final HttpStatus status;
  private final String message;

  @Override
  public HttpStatus status() {
    return this.status;
  }

  @Override
  public String message() {
    return this.message;
  }
}
