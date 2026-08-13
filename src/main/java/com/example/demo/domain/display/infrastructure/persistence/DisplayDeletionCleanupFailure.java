package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayDeletionCleanupFailure")
public class DisplayDeletionCleanupFailure extends BaseTimeEntity {

  private static final int EXCEPTION_TYPE_MAX_LENGTH = 255;
  private static final int FAILURE_MESSAGE_MAX_LENGTH = 1000;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayDeletionCleanupFailureId")
  private Long id;

  @Column(nullable = false)
  private Long displayId;

  @Column(nullable = false)
  private LocalDateTime deletedAt;

  @Column(nullable = false)
  private int retryCount;

  @Column(nullable = false, length = EXCEPTION_TYPE_MAX_LENGTH)
  private String exceptionType;

  @Column(length = FAILURE_MESSAGE_MAX_LENGTH)
  private String failureMessage;

  protected DisplayDeletionCleanupFailure() {}

  private DisplayDeletionCleanupFailure(
      Long displayId,
      LocalDateTime deletedAt,
      int retryCount,
      String exceptionType,
      String failureMessage) {
    this.displayId = Objects.requireNonNull(displayId, "displayId must not be null.");
    this.deletedAt = Objects.requireNonNull(deletedAt, "deletedAt must not be null.");
    this.retryCount = retryCount;
    this.exceptionType =
        truncate(
            Objects.requireNonNull(exceptionType, "exceptionType must not be null."),
            EXCEPTION_TYPE_MAX_LENGTH);
    this.failureMessage = truncate(failureMessage, FAILURE_MESSAGE_MAX_LENGTH);
  }

  public static DisplayDeletionCleanupFailure from(
      Long displayId, LocalDateTime deletedAt, int retryCount, RuntimeException exception) {
    RuntimeException safeException =
        Objects.requireNonNull(exception, "exception must not be null.");
    return new DisplayDeletionCleanupFailure(
        displayId,
        deletedAt,
        retryCount,
        safeException.getClass().getName(),
        safeException.getMessage());
  }

  private static String truncate(String value, int maxLength) {
    if (value == null || value.length() <= maxLength) {
      return value;
    }
    return value.substring(0, maxLength);
  }
}
