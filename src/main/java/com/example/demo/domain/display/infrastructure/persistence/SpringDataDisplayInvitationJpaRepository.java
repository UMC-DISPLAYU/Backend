package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.type.DisplayInvitationStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataDisplayInvitationJpaRepository
    extends JpaRepository<DisplayInvitation, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select invitation from DisplayInvitation invitation where invitation.id = :invitationId")
  Optional<DisplayInvitation> findByIdForUpdate(@Param("invitationId") Long invitationId);

  boolean existsByDisplayIdAndInviteeUserIdValueAndStatusAndDeletedAtIsNull(
      Long displayId, Long inviteeUserId, DisplayInvitationStatus status);

  List<DisplayInvitation> findByInviteeUserIdValueAndStatusAndDeletedAtIsNullOrderByIdDesc(
      Long inviteeUserId, DisplayInvitationStatus status);

  List<DisplayInvitation> findByDisplayIdAndInviteeUserIdValueAndStatusAndDeletedAtIsNull(
      Long displayId, Long inviteeUserId, DisplayInvitationStatus status);
}
