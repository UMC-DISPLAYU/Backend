package com.example.demo.domain.notification.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.notification.application.message.NotificationMessage;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.type.NotificationType;
import com.example.demo.domain.notification.infrastructure.persistence.adapter.JpaNotificationRepositoryAdapter;
import com.example.demo.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/** 중복 방지 조회와 보관 정책(180일·100개) 삭제 쿼리를 실제 DB로 확인한다. */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class JpaNotificationRepositoryAdapterTest {

  @Autowired private SpringDataNotificationJpaRepository jpaRepository;
  @Autowired private EntityManager entityManager;

  private JpaNotificationRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    // 어댑터를 빈으로 올리면 다른 @DataJpaTest와 설정이 달라져 테스트 컨텍스트가 하나 더 생기므로 직접 만든다.
    adapter = new JpaNotificationRepositoryAdapter(jpaRepository);
  }

  @Test
  void findRecipientsAlreadyNotifiedMatchesSameTypeAndTargetOnly() {
    save(1L, started(7L));
    save(2L, started(8L));
    save(3L, NotificationMessages.coAuthorAdded("작품", 7L));

    assertThat(
            adapter.findRecipientsAlreadyNotified(
                NotificationType.EXHIBITION_STARTED, 7L, List.of(1L, 2L, 3L, 4L)))
        .containsExactly(1L);
  }

  @Test
  void deleteCreatedBeforeRemovesOnlyOlderNotifications() {
    Notification old = save(1L, started(7L));
    Notification recent = save(1L, started(8L));
    updateCreatedAt(old.getId(), LocalDateTime.of(2026, 5, 1, 0, 0));
    updateCreatedAt(recent.getId(), LocalDateTime.of(2026, 5, 3, 0, 0));

    assertThat(adapter.deleteCreatedBefore(LocalDateTime.of(2026, 5, 2, 0, 0))).isEqualTo(1);
    assertThat(jpaRepository.findAll())
        .extracting(Notification::getId)
        .containsExactly(recent.getId());
  }

  @Test
  void deleteOlderThanLatestKeepsOnlyNewestNotificationsOfThatRecipient() {
    List<Notification> mine =
        IntStream.range(0, 5).mapToObj(i -> save(1L, started((long) i))).toList();
    Notification others = save(2L, started(1L));

    assertThat(adapter.findRecipientsHavingMoreThan(3)).containsExactly(1L);
    assertThat(adapter.deleteOlderThanLatest(1L, 3)).isEqualTo(2);
    assertThat(jpaRepository.findAll())
        .extracting(Notification::getId)
        .containsExactlyInAnyOrder(
            mine.get(2).getId(), mine.get(3).getId(), mine.get(4).getId(), others.getId());
  }

  private static NotificationMessage started(Long displayId) {
    return NotificationMessages.exhibitionStarted(
        "전시", LocalDate.of(2026, 12, 4), LocalDate.of(2026, 12, 6), null, displayId);
  }

  private Notification save(Long recipientUserId, NotificationMessage message) {
    return jpaRepository.saveAndFlush(
        Notification.create(
            recipientUserId, message.type(), message.title(), message.body(), message.target()));
  }

  private void updateCreatedAt(Long id, LocalDateTime createdAt) {
    entityManager
        .createQuery("UPDATE Notification n SET n.createdAt = :createdAt WHERE n.id = :id")
        .setParameter("createdAt", createdAt)
        .setParameter("id", id)
        .executeUpdate();
    entityManager.clear();
  }
}
