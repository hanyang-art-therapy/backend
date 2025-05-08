package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserException implements ExceptionType {
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "다시 로그인해주세요."), // 토큰 만료됐을 때
  FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
  DUPLICATE_USER_ID(HttpStatus.CONFLICT, "이미 등록된 학번입니다."),
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
  DUPLICATE_STUDENT_NO(HttpStatus.CONFLICT, "이미 등록된 학번입니다."),
  USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "일치하는 계정을 찾을 수 없습니다."),
  ERROR_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
  EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 실패"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류로 등록이 실패했습니다.");

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
