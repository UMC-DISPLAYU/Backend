package com.example.demo.domain.notification.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.notification.domain.type.NotificationTargetType;
import com.example.demo.domain.notification.domain.type.NotificationType;
import com.example.demo.domain.notification.domain.vo.NotificationBodySegment;
import com.example.demo.domain.notification.domain.vo.NotificationTarget;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class NotificationTest {

  private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 11, 16, 9, 0);

  @Test
  void isReadAsOfReturnsFalseWhenInboxWasNeverOpened() {
    Notification notification = notificationCreatedAt(CREATED_AT);

    assertThat(notification.isReadAsOf(null)).isFalse();
  }

  @Test
  void isReadAsOfReturnsTrueWhenCreatedBeforeOrAtLastReadAt() {
    Notification notification = notificationCreatedAt(CREATED_AT);

    assertThat(notification.isReadAsOf(CREATED_AT)).isTrue();
    assertThat(notification.isReadAsOf(CREATED_AT.plusSeconds(1))).isTrue();
  }

  @Test
  void isReadAsOfReturnsFalseWhenCreatedAfterLastReadAt() {
    Notification notification = notificationCreatedAt(CREATED_AT);

    assertThat(notification.isReadAsOf(CREATED_AT.minusNanos(1))).isFalse();
  }

  @Test
  void createCopiesBodySoLaterChangesDoNotLeakIn() {
    List<NotificationBodySegment> body =
        new ArrayList<>(List.of(NotificationBodySegment.plain("처음 문장")));

    Notification notification =
        Notification.create(1L, NotificationType.OPERATOR_NOTICE, "안내", body, null);
    body.add(NotificationBodySegment.plain("나중에 추가"));

    assertThat(notification.getBody()).containsExactly(NotificationBodySegment.plain("처음 문장"));
  }

  @Test
  void getTargetReturnsNullWhenNotificationDoesNotNavigate() {
    Notification notification =
        Notification.create(
            1L,
            NotificationType.EXHIBITION_DELETED,
            "전시 삭제",
            List.of(NotificationBodySegment.plain("전시가 삭제됐어요.")),
            null);

    assertThat(notification.getTarget()).isNull();
  }

  @Test
  void getTargetKeepsTypeAndIdWhenNotificationNavigates() {
    Notification notification =
        Notification.create(
            1L,
            NotificationType.ARTIST_NEW_EXHIBITION,
            "저장한 작가의 새 전시",
            List.of(NotificationBodySegment.bold("재생 중")),
            new NotificationTarget(NotificationTargetType.DISPLAY_DETAIL, 7L));

    assertThat(notification.getTarget())
        .isEqualTo(new NotificationTarget(NotificationTargetType.DISPLAY_DETAIL, 7L));
  }

  private Notification notificationCreatedAt(LocalDateTime createdAt) {
    Notification notification =
        Notification.create(
            1L,
            NotificationType.REQUEST_APPROVED,
            "등록 요청 승인",
            List.of(NotificationBodySegment.plain("승인됐어요.")),
            null);
    ReflectionTestUtils.setField(notification, "createdAt", createdAt);
    return notification;
  }
}
