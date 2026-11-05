package com.example.demo.domain.admin.application.query;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Set;

public record ContentSearchQuery(
    String q, String contentType, String operationStatus, String cursor, int size) {
  public ContentSearchQuery {
    q = q == null || q.isBlank() ? null : q.strip();
    if ((q != null && q.length() > 100)
        || (contentType != null && !Set.of("DISPLAY", "ARTWORK").contains(contentType))
        || (operationStatus != null && !Set.of("ACTIVE", "SUSPENDED").contains(operationStatus))
        || (cursor != null && (cursor.isBlank() || cursor.length() > 1024))
        || size < 1
        || size > 100) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
  }
}
