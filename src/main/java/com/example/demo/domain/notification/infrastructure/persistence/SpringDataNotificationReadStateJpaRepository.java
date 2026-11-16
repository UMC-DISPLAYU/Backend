package com.example.demo.domain.notification.infrastructure.persistence;

import com.example.demo.domain.notification.domain.aggregate.NotificationReadState;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataNotificationReadStateJpaRepository
    extends JpaRepository<NotificationReadState, Long> {

  // 같은 사용자가 두 탭에서 동시에 알림함을 열어도 기본 키 충돌 없이 한 행만 남도록 조회 후 저장 대신 upsert를 쓴다.
  // 늦게 도착한 요청이 더 이른 시각으로 덮어쓰지 않도록 둘 중 늦은 시각을 남긴다.
  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO NotificationReadState (userId, lastReadAt)
          VALUES (:userId, :readAt)
          ON DUPLICATE KEY UPDATE lastReadAt = GREATEST(lastReadAt, :readAt)
          """,
      nativeQuery = true)
  void upsertLastReadAt(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);
}
