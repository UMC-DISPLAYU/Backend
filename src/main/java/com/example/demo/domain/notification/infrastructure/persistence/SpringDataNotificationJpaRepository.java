package com.example.demo.domain.notification.infrastructure.persistence;

import com.example.demo.domain.notification.domain.aggregate.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataNotificationJpaRepository extends JpaRepository<Notification, Long> {

  // 알림은 생성 순서대로 id가 커지므로 id만으로 최신순 정렬과 커서를 처리한다.
  @Query(
      """
      SELECT n
      FROM Notification n
      WHERE n.recipientUserId = :recipientUserId
        AND (:cursorId IS NULL OR n.id < :cursorId)
      ORDER BY n.id DESC
      """)
  List<Notification> findByRecipientBeforeCursorOrderByIdDesc(
      @Param("recipientUserId") Long recipientUserId,
      @Param("cursorId") Long cursorId,
      Pageable pageable);

  boolean existsByRecipientUserId(Long recipientUserId);

  boolean existsByRecipientUserIdAndCreatedAtAfter(Long recipientUserId, LocalDateTime createdAt);
}
