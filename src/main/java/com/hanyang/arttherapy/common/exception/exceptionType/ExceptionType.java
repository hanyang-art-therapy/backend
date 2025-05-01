package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

public interface ExceptionType {

  HttpStatus status();

  String message();

  default int getHttpStatus() {
    return status().value();
  }
}
