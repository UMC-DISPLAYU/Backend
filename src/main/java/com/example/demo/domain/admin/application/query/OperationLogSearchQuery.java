package com.example.demo.domain.admin.application.query;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.Instant;
import java.util.regex.Pattern;

/** from 이상 to 미만의 처리 시각, 대상(종류+ID), 조치, 처리자, 사유 부분 일치로 거른다. */
public record OperationLogSearchQuery(
    String q,
    Instant from,
    Instant to,
    String targetType,
    Long targetId,
    String action,
    Long operatorId,
    Long cursor,
    int size) {
  private static final Pattern CODE = Pattern.compile("[A-Z_]{1,40}");

  public OperationLogSearchQuery {
    q = q == null || q.isBlank() ? null : q.strip();
    if ((q != null && q.length() > 100)
        || (from != null && to != null && !from.isBefore(to))
        || (targetType != null && !CODE.matcher(targetType).matches())
        || (targetId != null && (targetId <= 0 || targetType == null))
        || (action != null && !CODE.matcher(action).matches())
        || (operatorId != null && operatorId <= 0)
        || (cursor != null && cursor <= 0)
        || size < 1
        || size > 100) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
  }
}
