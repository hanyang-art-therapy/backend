package com.hanyang.arttherapy.common.exception.exceptionType;

import org.springframework.http.HttpStatus;

import lombok.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum FileSystemExceptionType implements ExceptionType {
  DIRECTORY_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "디렉토리 생성에 실패했습니다."),
  FILE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),
  FILE_EXTENSION_MISSING(HttpStatus.BAD_REQUEST, "파일에 확장자가 없습니다."),
  FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기가 너무 큽니다. 최대 5MB까지 업로드 가능합니다."),
  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
  FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
  INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),
  FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
  ;

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
