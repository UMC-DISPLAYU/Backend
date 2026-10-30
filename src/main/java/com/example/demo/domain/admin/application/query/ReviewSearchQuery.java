package com.example.demo.domain.admin.application.query;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Set;

/** keyword는 앞뒤 공백을 제거하고, 비어 있으면 null로 둔다. */
public record ReviewSearchQuery(String status, String keyword, Long cursor, int size) {
  public ReviewSearchQuery {
    keyword = keyword == null || keyword.isBlank() ? null : keyword.strip();
    if (!Set.of("PENDING_REVIEW", "PUBLISHED", "REJECTED").contains(status == null ? "" : status)
        || (keyword != null && keyword.length() > 100)
        || (cursor != null && cursor <= 0)
        || size < 1
        || size > 100) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
  }
}
