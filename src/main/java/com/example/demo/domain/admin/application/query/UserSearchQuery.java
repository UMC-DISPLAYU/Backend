package com.example.demo.domain.admin.application.query;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Set;

public record UserSearchQuery(String q, String accountStatus, Long cursor, int size) {
  public UserSearchQuery {
    q = q == null || q.isBlank() ? null : q.strip();
    if ((q != null && q.length() > 100)
        || (accountStatus != null && !Set.of("ACTIVE", "SUSPENDED").contains(accountStatus))
        || (cursor != null && cursor <= 0)
        || size < 1
        || size > 100) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
  }
}
