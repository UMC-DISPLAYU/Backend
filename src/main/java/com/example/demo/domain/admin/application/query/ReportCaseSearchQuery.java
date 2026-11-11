package com.example.demo.domain.admin.application.query;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Set;

public record ReportCaseSearchQuery(String status, Long cursor, int size) {
  public ReportCaseSearchQuery {
    if (status == null
        || !Set.of("PENDING", "RESOLVED").contains(status)
        || (cursor != null && cursor <= 0)
        || size < 1
        || size > 100) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
  }
}
