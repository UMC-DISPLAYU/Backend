package com.example.demo.domain.report.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements BaseErrorCode {
  CONTENT_REPORT_ALREADY_EXISTS(
      HttpStatus.CONFLICT, "CONTENT_REPORT_ALREADY_EXISTS", "이미 신고한 댓글입니다."),
  SELF_REPORT_NOT_ALLOWED(
      HttpStatus.FORBIDDEN, "SELF_REPORT_NOT_ALLOWED", "본인이 작성한 댓글은 신고할 수 없습니다.");
  private final HttpStatus status;
  private final String code;
  private final String message;
}
