package com.example.demo.domain.admin.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 운영자 조치 한 건. 대상이 삭제·해제된 뒤에도 남기며 수정하지 않는다. */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "AdminOperation",
    indexes = {
      @Index(name = "IDX_ADMIN_OPERATION_TARGET", columnList = "targetType, targetId, operationId"),
      @Index(name = "IDX_ADMIN_OPERATION_OPERATOR", columnList = "operatorId, operationId")
    })
public class AdminOperationJpaEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long operationId;

  @Column(nullable = false, length = 40)
  private String targetType;

  @Column(nullable = false)
  private Long targetId;

  @Column(nullable = false, length = 40)
  private String action;

  @Column(nullable = false)
  private Long operatorId;

  @Column(nullable = false, length = 1000)
  private String reason;

  @Column(nullable = false)
  private LocalDateTime processedAt;

  public AdminOperationJpaEntity(
      String targetType,
      Long targetId,
      String action,
      Long operatorId,
      String reason,
      LocalDateTime processedAt) {
    this.targetType = Objects.requireNonNull(targetType, "targetType must not be null.");
    this.targetId = Objects.requireNonNull(targetId, "targetId must not be null.");
    this.action = Objects.requireNonNull(action, "action must not be null.");
    this.operatorId = Objects.requireNonNull(operatorId, "operatorId must not be null.");
    this.reason = Objects.requireNonNull(reason, "reason must not be null.");
    this.processedAt = Objects.requireNonNull(processedAt, "processedAt must not be null.");
  }
}
