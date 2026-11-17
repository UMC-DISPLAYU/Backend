package com.example.demo.domain.notification.infrastructure.persistence.adapter;

import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.repository.NotificationRepository;
import com.example.demo.domain.notification.infrastructure.persistence.SpringDataNotificationJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
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
}
