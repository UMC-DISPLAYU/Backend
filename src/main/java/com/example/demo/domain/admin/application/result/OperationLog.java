package com.example.demo.domain.admin.application.result;

import java.time.Instant;

/** 운영자 조치 기록. 저장 전에는 operationId가 null이다. */
public record OperationLog(
    Long operationId,
    String targetType,
    Long targetId,
    String action,
    Long operatorId,
    String reason,
    Instant processedAt) {
  public static OperationLog of(
      String targetType,
      Long targetId,
      String action,
      Long operatorId,
      String reason,
      Instant processedAt) {
    return new OperationLog(null, targetType, targetId, action, operatorId, reason, processedAt);
  }
}
