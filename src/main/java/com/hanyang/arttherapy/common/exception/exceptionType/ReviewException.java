package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

public enum ReviewException implements ExceptionType {
  REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),
  REVIEW_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "리뷰 저장에 실패했습니다."),
  REVIEW_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류로 수정이 실패했습니다."),
  REVIEW_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류로 삭제가 실패했습니다."),
  REVIEW_TEXT_REQUIRED(HttpStatus.BAD_REQUEST, "수정할 내용은 필수입니다."),
  NOT_REVIEW_OWNER(HttpStatus.UNAUTHORIZED, "댓글 작성자만 수정할 수 있습니다."),
  SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류로 등록이 실패했습니다."),
  NOT_REVIEW_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 작성자만 삭제할 수 있습니다."),
  REVIEW_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류로 댓글 조회에 실패했습니다.");

  private final HttpStatus status;
  private final String message;

  ReviewException(HttpStatus status, String message) {
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
