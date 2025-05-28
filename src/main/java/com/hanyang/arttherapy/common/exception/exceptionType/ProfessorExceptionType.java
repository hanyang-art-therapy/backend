package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

public enum ProfessorExceptionType implements ExceptionType {
  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 파일을 찾을 수 없습니다."),
  UNAUTHORIZED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
  PROFESSOR_CREATE_FAIL(HttpStatus.BAD_REQUEST, "교수진 등록에 실패했습니다."),
  PROFESSOR_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 교수를 찾을 수 없습니다.");

  private final HttpStatus status;
  private final String message;

  ProfessorExceptionType(HttpStatus status, String message) {
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
