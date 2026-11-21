package com.example.demo.domain.notification.infrastructure.persistence.adapter;

import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.repository.NotificationRepository;
import com.example.demo.domain.notification.domain.type.NotificationType;
import com.example.demo.domain.notification.infrastructure.persistence.SpringDataNotificationJpaRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaNotificationRepositoryAdapter implements NotificationRepository {

  private final SpringDataNotificationJpaRepository jpaRepository;

  public JpaNotificationRepositoryAdapter(SpringDataNotificationJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<Notification> findByRecipientBeforeCursorOrderByIdDesc(
      Long recipientUserId, Long cursorId, int limit) {
    return jpaRepository.findByRecipientBeforeCursorOrderByIdDesc(
        recipientUserId, cursorId, PageRequest.of(0, limit));
  }

  @Override
  public boolean existsByRecipientCreatedAfter(Long recipientUserId, LocalDateTime createdAfter) {
    if (createdAfter == null) {
      return jpaRepository.existsByRecipientUserId(recipientUserId);
    }
    return jpaRepository.existsByRecipientUserIdAndCreatedAtAfter(recipientUserId, createdAfter);
  }

  @Override
  public List<Notification> saveAll(List<Notification> notifications) {
    return jpaRepository.saveAll(notifications);
  }

  @Override
  public Set<Long> findRecipientsAlreadyNotified(
      NotificationType type, Long targetId, Collection<Long> recipientUserIds) {
    if (targetId == null || recipientUserIds.isEmpty()) {
      return Set.of();
    }
    return new HashSet<>(
        jpaRepository.findRecipientsAlreadyNotified(type, targetId, recipientUserIds));
  }

  @Override
  public int deleteCreatedBefore(LocalDateTime createdBefore) {
    return jpaRepository.deleteCreatedBefore(createdBefore);
  }

  @Override
  public List<Long> findRecipientsHavingMoreThan(int keepCount) {
    return jpaRepository.findRecipientsHavingMoreThan(keepCount);
  }

  @Override
  public int deleteOlderThanLatest(Long recipientUserId, int keepCount) {
    // keepCount번째로 최신인 알림을 찾고, 그보다 ID가 작은(더 오래된) 알림을 지운다.
    List<Long> lastKept =
        jpaRepository.findIdsByRecipientOrderByIdDesc(
            recipientUserId, PageRequest.of(keepCount - 1, 1));
    if (lastKept.isEmpty()) {
      return 0;
    }
    return jpaRepository.deleteByRecipientAndIdLessThan(recipientUserId, lastKept.get(0));
  }
}
