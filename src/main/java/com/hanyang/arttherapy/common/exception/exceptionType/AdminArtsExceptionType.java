package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

public enum AdminArtsExceptionType implements ExceptionType {
  ARTS_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 작품이 존재하지 않습니다."),
  ARTIST_LIST_NULL(HttpStatus.BAD_REQUEST, "작가 리스트가 누락되었습니다."),
  GALLERY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 전시회가 존재하지 않습니다."),
  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 파일이 존재하지 않습니다."),
  ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 작가가 존재하지 않습니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "관리자 권한이 없습니다."),
  SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류로 요청이 실패했습니다.");

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
