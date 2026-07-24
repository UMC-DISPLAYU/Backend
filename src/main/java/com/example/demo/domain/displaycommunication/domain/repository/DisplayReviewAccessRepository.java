package com.example.demo.domain.displaycommunication.domain.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface DisplayReviewAccessRepository {
  Optional<DisplayReviewAccess> findByDisplayId(Long displayId);

  Optional<DisplayReviewAccess> findByDisplayIdAndUserId(Long displayId, Long userId);

  Set<Long> findAcceptedTeamMemberUserIds(Long displayId);

  record DisplayReviewAccess(
      Long ownerUserId,
      LocalDate startDate,
      LocalDate endDate,
      boolean published,
      boolean acceptedTeamMember) {}
}
