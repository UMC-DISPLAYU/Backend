package com.example.demo.domain.display.domain.entity;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.vo.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private LocalDateTime deletedAt;

  protected DisplayInvitation() {}

  public DisplayInvitation(
      Long id,
      UserId inviterUserId,
      UserId inviteeUserId,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {
    this.id = id;
    this.inviterUserId = Objects.requireNonNull(inviterUserId, "inviterUserId must not be null.");
    this.inviteeUserId = Objects.requireNonNull(inviteeUserId, "inviteeUserId must not be null.");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null.");
    this.deletedAt = deletedAt;
  }

  public void assignDisplay(Display display) {
    this.display = Objects.requireNonNull(display, "display must not be null.");
  }

  public void reject() {
    this.deletedAt = LocalDateTime.now();
  }

  public void restore() {
    this.deletedAt = null;
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }

  @PrePersist
  private void prePersist() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
