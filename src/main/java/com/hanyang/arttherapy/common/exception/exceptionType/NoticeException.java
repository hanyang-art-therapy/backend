package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

public enum NoticeException implements ExceptionType {
  NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
  NOTICE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "공지사항 저장에 실패했습니다."),
  NOTICE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "공지사항 수정에 실패했습니다."),
  NOTICE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "공지사항 삭제에 실패했습니다."),
  NOT_NOTICE_OWNER(HttpStatus.UNAUTHORIZED, "관리자만 수정할 수 있습니다."),
  INVALID_NOTICE_REQUEST(HttpStatus.BAD_REQUEST, "공지사항 요청 형식이 잘못되었습니다."),
  NOTICE_FILE_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "공지사항 첨부파일 조회에 실패했습니다.");

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
