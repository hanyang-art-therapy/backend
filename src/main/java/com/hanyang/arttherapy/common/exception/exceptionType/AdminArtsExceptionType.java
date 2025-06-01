package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

public enum AdminArtsExceptionType implements ExceptionType {
  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일이 존재하지 않습니다."),
  GALLERY_NOT_FOUND(HttpStatus.NOT_FOUND, "전시회가 존재하지 않습니다."),
  ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "작가가 존재하지 않습니다."),
  ARTS_NOT_FOUND(HttpStatus.NOT_FOUND, "작품이 존재하지 않습니다."),
  UNAUTHORIZED(HttpStatus.FORBIDDEN, "관리자 권한이 없습니다.");

  private final HttpStatus status;
  private final String message;

  AdminArtsExceptionType(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  @Override
  public HttpStatus status() {
    return status;
  }

  @Override
  public String message() {
    return message;
  }
}
