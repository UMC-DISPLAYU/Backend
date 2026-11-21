package com.example.demo.domain.notification.domain.repository;

import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.type.NotificationType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface NotificationRepository {

  /** cursorId보다 오래된 알림을 최신순으로 limit개 조회한다. cursorId가 null이면 가장 최신부터 조회한다. */
  List<Notification> findByRecipientBeforeCursorOrderByIdDesc(
      Long recipientUserId, Long cursorId, int limit);

  /** createdAfter 이후에 생긴 알림이 있는지 확인한다. createdAfter가 null이면 알림이 하나라도 있는지 확인한다. */
  boolean existsByRecipientCreatedAfter(Long recipientUserId, LocalDateTime createdAfter);

  List<Notification> saveAll(List<Notification> notifications);

  /** 주어진 사람 중 같은 종류·같은 대상 ID의 알림을 이미 받은 사람을 찾는다. */
  Set<Long> findRecipientsAlreadyNotified(
      NotificationType type, Long targetId, Collection<Long> recipientUserIds);

  /** createdBefore보다 먼저 만들어진 알림을 모두 지우고 지운 수를 돌려준다. */
  int deleteCreatedBefore(LocalDateTime createdBefore);

  /** 알림이 keepCount개보다 많은 사람을 찾는다. */
  List<Long> findRecipientsHavingMoreThan(int keepCount);

  /** 그 사람의 최신 keepCount개만 남기고 더 오래된 알림을 지운 뒤 지운 수를 돌려준다. */
  int deleteOlderThanLatest(Long recipientUserId, int keepCount);
}
