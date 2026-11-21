package com.example.demo.domain.notification.infrastructure.persistence;

import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.type.NotificationType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

  @Query(
      """
      SELECT DISTINCT n.recipientUserId
      FROM Notification n
      WHERE n.type = :type
        AND n.targetId = :targetId
        AND n.recipientUserId IN :recipientUserIds
      """)
  List<Long> findRecipientsAlreadyNotified(
      @Param("type") NotificationType type,
      @Param("targetId") Long targetId,
      @Param("recipientUserIds") Collection<Long> recipientUserIds);

  @Modifying
  @Query("DELETE FROM Notification n WHERE n.createdAt < :createdBefore")
  int deleteCreatedBefore(@Param("createdBefore") LocalDateTime createdBefore);

  @Query(
      """
      SELECT n.recipientUserId
      FROM Notification n
      GROUP BY n.recipientUserId
      HAVING COUNT(n) > :keepCount
      """)
  List<Long> findRecipientsHavingMoreThan(@Param("keepCount") long keepCount);

  /** 최신순으로 정렬해 Pageable 위치의 알림 ID를 찾는다. 남길 마지막 알림의 ID를 구할 때 쓴다. */
  @Query(
      """
      SELECT n.id
      FROM Notification n
      WHERE n.recipientUserId = :recipientUserId
      ORDER BY n.id DESC
      """)
  List<Long> findIdsByRecipientOrderByIdDesc(
      @Param("recipientUserId") Long recipientUserId, Pageable pageable);

  @Modifying
  @Query("DELETE FROM Notification n WHERE n.recipientUserId = :recipientUserId AND n.id < :id")
  int deleteByRecipientAndIdLessThan(
      @Param("recipientUserId") Long recipientUserId, @Param("id") Long id);
}
