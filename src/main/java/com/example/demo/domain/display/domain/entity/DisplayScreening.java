package com.example.demo.domain.display.domain.entity;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.type.DisplayScreeningStatus;
import com.example.demo.global.error.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayScreening")
public class DisplayScreening {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "screeningId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "displayId", nullable = false)
  private Display display;

  @Column(nullable = false)
  private Long requesterId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DisplayScreeningStatus status;

  @Column(nullable = false)
  private LocalDateTime requestedAt;

  private Long reviewerId;

  private LocalDateTime processedAt;

  @Column(length = 1000)
  private String rejectionReason;

  protected DisplayScreening() {}

  private DisplayScreening(Display display, Long requesterId, LocalDateTime requestedAt) {
    this.display = Objects.requireNonNull(display, "display must not be null.");
    this.requesterId = Objects.requireNonNull(requesterId, "requesterId must not be null.");
    this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt must not be null.");
    this.status = DisplayScreeningStatus.PENDING_REVIEW;
  }

  public static DisplayScreening request(
      Display display, Long requesterId, LocalDateTime requestedAt) {
    return new DisplayScreening(display, requesterId, requestedAt);
  }

  public void approve(Long reviewerId, LocalDateTime processedAt) {
    requirePending();
    this.status = DisplayScreeningStatus.PUBLISHED;
    this.reviewerId = Objects.requireNonNull(reviewerId, "reviewerId must not be null.");
    this.processedAt = Objects.requireNonNull(processedAt, "processedAt must not be null.");
    this.rejectionReason = null;
  }

  public void reject(Long reviewerId, String reason, LocalDateTime processedAt) {
    requirePending();
    if (reason == null || reason.isBlank() || reason.length() > 1000) {
      throw new IllegalArgumentException("reason must be between 1 and 1000 characters.");
    }
    this.status = DisplayScreeningStatus.REJECTED;
    this.reviewerId = Objects.requireNonNull(reviewerId, "reviewerId must not be null.");
    this.processedAt = Objects.requireNonNull(processedAt, "processedAt must not be null.");
    this.rejectionReason = reason;
  }

  private void requirePending() {
    if (status != DisplayScreeningStatus.PENDING_REVIEW) {
      throw new BusinessException(DisplayErrorCode.INVALID_DISPLAY_REVIEW_STATUS);
    }
  }
}
