package com.example.demo.domain.admin.presentation.response;

import java.time.Instant;
import java.util.List;

public final class UserResponses {
  private UserResponses() {}

  public record Summary(
      Long userId, String nickname, String email, String accountStatus, Instant createdAt) {}

  public record Page(List<Summary> items, Long nextCursor, boolean hasNext) {}

  public record Detail(
      Long userId,
      String nickname,
      String email,
      String accountStatus,
      Instant createdAt,
      Instant suspendedAt,
      Long operatorId,
      String reason,
      List<History> history) {}

  public record History(
      Long operationId, String action, Long operatorId, String reason, Instant processedAt) {}
}
