package com.example.demo.domain.notification.domain.aggregate;

import com.example.demo.domain.notification.domain.type.NotificationTargetType;
import com.example.demo.domain.notification.domain.type.NotificationType;
import com.example.demo.domain.notification.domain.vo.NotificationBodyConverter;
import com.example.demo.domain.notification.domain.vo.NotificationBodySegment;
import com.example.demo.domain.notification.domain.vo.NotificationTarget;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** 한 사용자의 알림함에 쌓이는 알림 한 건. 제목과 내용은 만들어지는 시점의 완성된 문장으로 저장해서, 원본 전시·작품이 나중에 삭제돼도 알림은 그대로 남는다. */
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "Notification",
    indexes =
        @Index(
            name = "IDX_NOTIFICATION_RECIPIENT_ID",
            columnList = "recipientUserId, notificationId"))
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notificationId")
  private Long id;

  @Column(nullable = false)
  private Long recipientUserId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private NotificationType type;

  @Column(nullable = false, length = 100)
  private String title;

  @Convert(converter = NotificationBodyConverter.class)
  @Column(nullable = false, columnDefinition = "TEXT")
  private List<NotificationBodySegment> body;

  @Enumerated(EnumType.STRING)
  @Column(length = 30)
  private NotificationTargetType targetType;

  @Column private Long targetId;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  protected Notification() {}

  private Notification(
      Long recipientUserId,
      NotificationType type,
      String title,
      List<NotificationBodySegment> body,
      NotificationTarget target) {
    this.recipientUserId =
        Objects.requireNonNull(recipientUserId, "recipientUserId must not be null.");
    this.type = Objects.requireNonNull(type, "type must not be null.");
    this.title = Objects.requireNonNull(title, "title must not be null.");
    this.body = List.copyOf(Objects.requireNonNull(body, "body must not be null."));
    if (target != null) {
      this.targetType = target.type();
      this.targetId = target.id();
    }
  }

  public static Notification create(
      Long recipientUserId,
      NotificationType type,
      String title,
      List<NotificationBodySegment> body,
      NotificationTarget target) {
    return new Notification(recipientUserId, type, title, body, target);
  }

  public NotificationTarget getTarget() {
    return targetType == null ? null : new NotificationTarget(targetType, targetId);
  }

  /** 알림함을 마지막으로 연 시각 이후에 생긴 알림만 안 읽음이다. 한 번도 연 적 없으면 모두 안 읽음이다. */
  public boolean isReadAsOf(LocalDateTime lastReadAt) {
    return lastReadAt != null && !createdAt.isAfter(lastReadAt);
  }
}
