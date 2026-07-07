package com.example.demo.global.response;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

public record ApiResponseBody<T>(
    ResultType resultType, SuccessBody<T> success, ErrorBody error, Meta meta) {

  public static <T> ApiResponseBody<T> success(T data, HttpServletRequest request) {
    return new ApiResponseBody<>(
        ResultType.SUCCESS, new SuccessBody<>(data), null, Meta.from(request));
  }

  public static ApiResponseBody<Void> fail(ErrorBody error, HttpServletRequest request) {
    return new ApiResponseBody<>(ResultType.FAIL, null, error, Meta.from(request));
  }

  public record SuccessBody<T>(T data) {}

  public record ErrorBody(String code, String message, Object details) {}

  public record Meta(LocalDateTime timestamp, String path) {

    private static Meta from(HttpServletRequest request) {
      return new Meta(LocalDateTime.now(), request.getRequestURI());
    }
  }
}
