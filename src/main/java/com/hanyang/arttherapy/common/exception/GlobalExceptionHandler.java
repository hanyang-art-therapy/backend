package com.hanyang.arttherapy.common.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import com.hanyang.arttherapy.common.exception.exceptionType.FileSystemExceptionType;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<Map<String, String>> handleCustomException(CustomException e) {
    return ResponseEntity.status(e.getExceptionType().status())
        .body(Map.of("message", e.getMessage()));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("message", e.getMessage()));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<Map<String, String>> handleMaxSizeException(
      MaxUploadSizeExceededException e) {
    String errorMessage = FileSystemExceptionType.FILE_SIZE_EXCEEDED.getMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", errorMessage));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<CommonMessageResponse> handleResponseStatusException(
      ResponseStatusException e) {
    return ResponseEntity.status(e.getStatusCode()).body(new CommonMessageResponse(e.getReason()));
  }
}
