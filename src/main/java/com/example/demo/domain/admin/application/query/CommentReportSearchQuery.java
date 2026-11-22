package com.example.demo.domain.admin.application.query;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;

public record CommentReportSearchQuery(Long cursor, int size) {
  public CommentReportSearchQuery {
    if ((cursor != null && cursor <= 0) || size < 1 || size > 100) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
  }
}
