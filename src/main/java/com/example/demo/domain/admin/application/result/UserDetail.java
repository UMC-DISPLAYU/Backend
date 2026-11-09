package com.example.demo.domain.admin.application.result;

import java.time.Instant;
import java.util.List;

public record UserDetail(
    Long userId,
    String nickname,
    String email,
    String accountStatus,
    Instant createdAt,
    Instant suspendedAt,
    Long operatorId,
    String reason,
    List<History> history) {
  public record History(
      Long operationId, String action, Long operatorId, String reason, Instant processedAt) {}
}
