package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

public enum GalleryExceptionType implements ExceptionType {
  GALLERY_NOT_FOUND(HttpStatus.NOT_FOUND, "전시회를 찾을 수 없습니다."),
  GALLERY_CREATE_FAIL(HttpStatus.BAD_REQUEST, "전시회 생성에 실패했습니다."),
  GALLERY_UPDATE_FAIL(HttpStatus.BAD_REQUEST, "전시회 수정에 실패했습니다."),
  GALLERY_DELETE_FAIL(HttpStatus.BAD_REQUEST, "전시회 삭제에 실패했습니다."),
  INVALID_GALLERY_DATE(HttpStatus.BAD_REQUEST, "전시회 날짜가 유효하지 않습니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "관리자 권한이 없습니다."),

  DUPLICATED_YEAR(HttpStatus.CONFLICT, "해당 연도에는 이미 전시회가 등록되어 있습니다.");
  private final HttpStatus status;
  private final String message;

  GalleryExceptionType(HttpStatus status, String message) {
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
