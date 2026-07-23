package com.example.demo.domain.display.domain.entity;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.type.DisplayInvitationStatus;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayInvitation")
public class DisplayInvitation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "disInvitationId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "displayId", nullable = false)
  private Display display;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "userId", nullable = false))
  private UserId inviterUserId;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "userId2", nullable = false))
  private UserId inviteeUserId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DisplayInvitationStatus status;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private LocalDateTime respondedAt;

  private LocalDateTime deletedAt;

  protected DisplayInvitation() {}

  public DisplayInvitation(
      Long id,
      UserId inviterUserId,
      UserId inviteeUserId,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {
    this(
        id,
        inviterUserId,
        inviteeUserId,
        DisplayInvitationStatus.PENDING,
        createdAt,
        null,
        deletedAt);
  }

  public DisplayInvitation(
      Long id,
      UserId inviterUserId,
      UserId inviteeUserId,
      DisplayInvitationStatus status,
      LocalDateTime createdAt,
      LocalDateTime respondedAt,
      LocalDateTime deletedAt) {
    this.id = id;
    this.inviterUserId = Objects.requireNonNull(inviterUserId, "inviterUserId must not be null.");
    this.inviteeUserId = Objects.requireNonNull(inviteeUserId, "inviteeUserId must not be null.");
    this.status = Objects.requireNonNull(status, "status must not be null.");
    this.createdAt = createdAt;
    this.respondedAt = respondedAt;
    this.deletedAt = deletedAt;
  }

  public void assignDisplay(Display display) {
    this.display = Objects.requireNonNull(display, "display must not be null.");
  }

  public void accept(LocalDateTime respondedAt) {
    ensurePending();
    this.status = DisplayInvitationStatus.ACCEPTED;
    this.respondedAt = Objects.requireNonNull(respondedAt, "respondedAt must not be null.");
  }

  public void reject(LocalDateTime respondedAt) {
    ensurePending();
    this.status = DisplayInvitationStatus.REJECTED;
    this.respondedAt = Objects.requireNonNull(respondedAt, "respondedAt must not be null.");
    this.deletedAt = this.respondedAt;
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }

  public boolean isPending() {
    return status == DisplayInvitationStatus.PENDING && !isDeleted();
  }

  public boolean isInvitee(Long userId) {
    return inviteeUserId.value().equals(userId);
  }

  private void ensurePending() {
    if (status == DisplayInvitationStatus.ACCEPTED) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_INVITATION_ALREADY_ACCEPTED);
    }
    if (status == DisplayInvitationStatus.REJECTED || isDeleted()) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_INVITATION_ALREADY_REJECTED);
    }
    if (status != DisplayInvitationStatus.PENDING) {
      throw new BusinessException(DisplayErrorCode.INVALID_DISPLAY_INVITATION_STATUS);
    }
  }

  @PrePersist
  private void prePersist() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
