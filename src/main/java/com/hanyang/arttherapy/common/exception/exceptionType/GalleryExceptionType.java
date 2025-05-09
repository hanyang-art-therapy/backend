package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

public enum GalleryExceptionType implements ExceptionType {
  GALLERY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 전시회를 찾을 수 없습니다."),
  GALLERY_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "전시회 삭제에 실패했습니다."),
  GALLERY_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "전시회 수정에 실패했습니다."),
  GALLERY_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "전시회 등록에 실패했습니다.");

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
