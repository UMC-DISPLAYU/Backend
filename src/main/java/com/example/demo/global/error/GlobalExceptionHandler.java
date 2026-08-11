package com.example.demo.global.error;

import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.response.ApiResponseBody.ErrorBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleBusinessException(
      BusinessException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = exception.errorCode();
    logErrorResponse(errorCode, request);
    ErrorBody error = new ErrorBody(errorCode.getCode(), exception.getMessage(), null);
    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleOptimisticLockingFailureException(
      OptimisticLockingFailureException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.CONFLICT;
    logErrorResponse(errorCode, request);
    ErrorBody error = new ErrorBody(errorCode.getCode(), errorCode.getMessage(), null);
    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    List<FieldErrorDetail> details =
        exception.getBindingResult().getFieldErrors().stream()
            .map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
            .toList();

    BaseErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;
    logErrorResponse(errorCode, request);
    ErrorBody error = new ErrorBody(errorCode.getCode(), errorCode.getMessage(), details);
    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleConstraintViolationException(
      ConstraintViolationException exception, HttpServletRequest request) {
    List<FieldErrorDetail> details =
        exception.getConstraintViolations().stream()
            .map(
                violation ->
                    new FieldErrorDetail(
                        violation.getPropertyPath().toString(), violation.getMessage()))
            .toList();

    BaseErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;
    logErrorResponse(errorCode, request);
    ErrorBody error = new ErrorBody(errorCode.getCode(), errorCode.getMessage(), details);
    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.INVALID_TYPE_VALUE;
    logErrorResponse(errorCode, request);
    ErrorBody error =
        new ErrorBody(
            errorCode.getCode(),
            errorCode.getMessage(),
            new FieldErrorDetail(exception.getName(), "요청 값의 타입이 올바르지 않습니다."));
    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.MISSING_REQUIRED_VALUE;
    logErrorResponse(errorCode, request);
    ErrorBody error =
        new ErrorBody(
            errorCode.getCode(),
            errorCode.getMessage(),
            new FieldErrorDetail(exception.getParameterName(), "필수 요청 파라미터입니다."));
    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponseBody.fail(error, request));
  }

  // 같은 경로를 파라미터로 분기하는 API에서, 어떤 분기 조건도 만족하지 못한 요청을 400으로 처리한다.
  @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleUnsatisfiedServletRequestParameterException(
      UnsatisfiedServletRequestParameterException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.MISSING_REQUIRED_VALUE;
    logErrorResponse(errorCode, request);
    ErrorBody error =
        new ErrorBody(
            errorCode.getCode(),
            errorCode.getMessage(),
            new FieldErrorDetail("parameters", "요청에 필요한 파라미터 조건을 만족하지 않습니다."));
    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.MALFORMED_JSON;
    logErrorResponse(errorCode, request);
    ErrorBody error = new ErrorBody(errorCode.getCode(), errorCode.getMessage(), null);
    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.METHOD_NOT_ALLOWED;
    logErrorResponse(errorCode, request);
    ErrorBody error = new ErrorBody(errorCode.getCode(), errorCode.getMessage(), null);
    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE;
    logErrorResponse(errorCode, request);
    ErrorBody error = new ErrorBody(errorCode.getCode(), errorCode.getMessage(), null);
    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiResponseBody<Void>> handleNoResourceFoundException(
      NoResourceFoundException exception, HttpServletRequest request) {
    BaseErrorCode errorCode = GlobalErrorCode.NOT_FOUND;
    logErrorResponse(errorCode, request);
    ErrorBody error = new ErrorBody(errorCode.getCode(), errorCode.getMessage(), null);
    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponseBody.fail(error, request));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseBody<Void>> handleException(
      Exception exception, HttpServletRequest request) {
    log.error("Unhandled exception occurred. uri={}", request.getRequestURI(), exception);
    BaseErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
    ErrorBody error = new ErrorBody(errorCode.getCode(), errorCode.getMessage(), null);
    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponseBody.fail(error, request));
  }

  private void logErrorResponse(BaseErrorCode errorCode, HttpServletRequest request) {
    String message = "HTTP error response. status={} code={} method={} uri={}";
    Object[] arguments = {
      errorCode.getStatus().value(),
      errorCode.getCode(),
      request.getMethod(),
      request.getRequestURI()
    };

    if (errorCode.getStatus().value() == 404) {
      log.info(message, arguments);
    } else if (errorCode.getStatus().is4xxClientError()) {
      log.warn(message, arguments);
    } else {
      log.error(message, arguments);
    }
  }

  public record FieldErrorDetail(String field, String message) {}
}
