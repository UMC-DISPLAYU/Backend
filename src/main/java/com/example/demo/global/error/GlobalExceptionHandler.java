package com.example.demo.global.error;

import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.response.ApiResponseBody.ErrorBody;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleBusinessException(
      BusinessException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = exception.errorCode();
    ErrorBody error = new ErrorBody(errorCode.code(), exception.getMessage(), null);
    return ResponseEntity.status(errorCode.status()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    List<FieldErrorDetail> details =
        exception.getBindingResult().getFieldErrors().stream()
            .map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
            .toList();

    BaseErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;
    ErrorBody error = new ErrorBody(errorCode.code(), errorCode.message(), details);
    return ResponseEntity.status(errorCode.status()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.INVALID_TYPE_VALUE;
    ErrorBody error =
        new ErrorBody(
            errorCode.code(),
            errorCode.message(),
            new FieldErrorDetail(exception.getName(), "요청 값의 타입이 올바르지 않습니다."));
    return ResponseEntity.status(errorCode.status()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.MISSING_REQUIRED_VALUE;
    ErrorBody error =
        new ErrorBody(
            errorCode.code(),
            errorCode.message(),
            new FieldErrorDetail(exception.getParameterName(), "필수 요청 파라미터입니다."));
    return ResponseEntity.status(errorCode.status()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.MALFORMED_JSON;
    ErrorBody error = new ErrorBody(errorCode.code(), errorCode.message(), null);
    return ResponseEntity.status(errorCode.status()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.METHOD_NOT_ALLOWED;
    ErrorBody error = new ErrorBody(errorCode.code(), errorCode.message(), null);
    return ResponseEntity.status(errorCode.status()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE;
    ErrorBody error = new ErrorBody(errorCode.code(), errorCode.message(), null);
    return ResponseEntity.status(errorCode.status()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseBody<Void>> handleException(
      Exception exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
    ErrorBody error = new ErrorBody(errorCode.code(), errorCode.message(), null);
    return ResponseEntity.status(errorCode.status()).body(ApiResponseBody.fail(error, request));
  }

  public record FieldErrorDetail(String field, String message) {}
}
