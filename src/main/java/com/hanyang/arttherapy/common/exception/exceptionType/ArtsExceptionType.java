package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ArtsExceptionType implements ExceptionType {
  ART_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 작품을 찾을 수 없습니다."),
  INVALID_ART_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 작품 요청입니다.");

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
