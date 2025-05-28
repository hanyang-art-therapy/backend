package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

public enum NoticeException implements ExceptionType {
  NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
  NOTICE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "공지사항 저장에 실패했습니다."),
  NOTICE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "공지사항 수정에 실패했습니다."),
  NOTICE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "공지사항 삭제에 실패했습니다."),
  NOT_NOTICE_OWNER(HttpStatus.FORBIDDEN, "관리자만 수정할 수 있습니다."),
  NOTICE_REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "제목과 내용, 카테고리 필수 입력 항목입니다."),
  NOT_NOTICE_DELETER(HttpStatus.FORBIDDEN, "관리자만 삭제할 수 있습니다."),
  NOTICE_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
  NOT_ADMIN(HttpStatus.FORBIDDEN, "관리자만 게시글 등록이 가능합니다."),
  UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");

  private final HttpStatus status;
  private final String message;

  NoticeException(HttpStatus status, String message) {
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
