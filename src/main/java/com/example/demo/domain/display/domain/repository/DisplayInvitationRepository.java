package com.example.demo.domain.display.domain.repository;

import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import java.util.Optional;

public interface DisplayInvitationRepository {

  Optional<DisplayInvitation> findById(Long invitationId);

  Optional<DisplayInvitation> findByIdForUpdate(Long invitationId);

  boolean existsPendingByDisplayIdAndInviteeUserId(Long displayId, Long inviteeUserId);

  DisplayInvitation save(DisplayInvitation invitation);
}
