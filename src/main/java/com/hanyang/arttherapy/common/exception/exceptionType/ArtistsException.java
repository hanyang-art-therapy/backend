package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ArtistsException implements ExceptionType {
  DUPLICATE_STUDENT_NO(HttpStatus.CONFLICT, "이미 등록된 학번입니다."),
  ARTIST_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 작가는 존재하지 않습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류로 등록이 실패했습니다."),
  ;

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
