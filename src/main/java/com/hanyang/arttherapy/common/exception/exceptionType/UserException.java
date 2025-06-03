package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserException implements ExceptionType {
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "토큰만료"), // 토큰 만료됐을 때
  FORBIDDEN(HttpStatus.FORBIDDEN, "다시 로그인해주세요"),
  USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
  EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
  STUDENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 학번입니다."),
  BLANK_REQUIRED(HttpStatus.BAD_REQUEST, "정보를 입력해 주세요."),
  USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "일치하는 회원을 찾을 수 없습니다."),
  USER_NOT_ACTIVE(HttpStatus.FORBIDDEN, "탈퇴한 회원입니다."),
  ERROR_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
  EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 실패"),
  EMAIL_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "이메일 인증에 실패했습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류로 등록이 실패했습니다."),
  INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 ACCESSTOKEN입니다."),
  INVALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 REFRESHTOKEN입니다."),
  USER_HISTORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 회원의 가입 이력을 찾을 수 없습니다."),
  NOT_ADMIN(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다."),
  UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");

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
