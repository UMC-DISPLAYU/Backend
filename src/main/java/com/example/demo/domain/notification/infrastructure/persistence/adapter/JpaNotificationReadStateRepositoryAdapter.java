package com.example.demo.domain.notification.infrastructure.persistence.adapter;

import com.example.demo.domain.notification.domain.aggregate.NotificationReadState;
import com.example.demo.domain.notification.domain.repository.NotificationReadStateRepository;
import com.example.demo.domain.notification.infrastructure.persistence.SpringDataNotificationReadStateJpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaNotificationReadStateRepositoryAdapter implements NotificationReadStateRepository {

  private final SpringDataNotificationReadStateJpaRepository jpaRepository;

  public JpaNotificationReadStateRepositoryAdapter(
      SpringDataNotificationReadStateJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<LocalDateTime> findLastReadAt(Long userId) {
    return jpaRepository.findById(userId).map(NotificationReadState::getLastReadAt);
  }

  @Override
  public void markReadAt(Long userId, LocalDateTime readAt) {
    jpaRepository.upsertLastReadAt(userId, readAt);
  }
}
