package com.hanyang.arttherapy.common.exception;

import com.hanyang.arttherapy.common.exception.exceptionType.ExceptionType;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
  private final ExceptionType exceptionType;

  public CustomException(ExceptionType exceptionType) {
    super(exceptionType.message());
    this.exceptionType = exceptionType;
  }
}
