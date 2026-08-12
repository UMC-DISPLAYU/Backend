package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataDisplayJpaRepository extends JpaRepository<Display, Long> {

  @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
  Optional<Display> findWithOptimisticLockById(Long displayId);

  Optional<Display> findByInvitationToken(String invitationToken);

  boolean existsByOwnerUserIdValueAndTitleAndDeletedAtIsNull(Long ownerUserId, String title);

  @Query(
      """
      SELECT DISTINCT display
      FROM Display display
      LEFT JOIN FETCH display.images image
      WHERE display.ownerUserId.value = :userId
        AND display.deletedAt IS NULL
      ORDER BY display.period.startDate DESC, display.id DESC
      """)
  List<Display> findCreatedDisplaysByUserId(@Param("userId") Long userId);

  @Query(
      """
      SELECT DISTINCT display
      FROM Display display
      JOIN display.teamMembers teamMember
      LEFT JOIN FETCH display.images image
      WHERE teamMember.userId.value = :userId
        AND teamMember.accepted = true
        AND display.ownerUserId.value <> :userId
        AND display.deletedAt IS NULL
      ORDER BY display.period.startDate DESC, display.id DESC
      """)
  List<Display> findParticipatedDisplaysByUserId(@Param("userId") Long userId);

  @Query(
      """
      SELECT DISTINCT display
      FROM Display display
      LEFT JOIN FETCH display.images image
      WHERE display.ownerUserId.value = :userId
        AND display.status = :status
        AND display.deletedAt IS NULL
      ORDER BY display.period.startDate DESC, display.id DESC
      """)
  List<Display> findPublishedCreatedDisplaysByUserId(
      @Param("userId") Long userId, @Param("status") DisplayStatus status);

  default List<Display> findPublishedCreatedDisplaysByUserId(Long userId) {
    return findPublishedCreatedDisplaysByUserId(userId, DisplayStatus.PUBLISHED);
  }

  @Query(
      """
      SELECT DISTINCT display
      FROM Display display
      JOIN display.teamMembers teamMember
      LEFT JOIN FETCH display.images image
      WHERE teamMember.userId.value = :userId
        AND teamMember.accepted = true
        AND display.ownerUserId.value <> :userId
        AND display.status = :status
        AND display.deletedAt IS NULL
      ORDER BY display.period.startDate DESC, display.id DESC
      """)
  List<Display> findPublishedParticipatedDisplaysByUserId(
      @Param("userId") Long userId, @Param("status") DisplayStatus status);

  default List<Display> findPublishedParticipatedDisplaysByUserId(Long userId) {
    return findPublishedParticipatedDisplaysByUserId(userId, DisplayStatus.PUBLISHED);
  }
}
