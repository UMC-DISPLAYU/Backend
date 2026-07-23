package com.example.demo.domain.displaycommunication.domain.repository;

import java.time.LocalDate;
import java.util.Optional;

public interface DisplayReviewAccessRepository {
  Optional<DisplayReviewAccess> findByDisplayIdAndUserId(Long displayId, Long userId);

  record DisplayReviewAccess(
      Long ownerUserId,
      LocalDate startDate,
      LocalDate endDate,
      boolean published,
      boolean acceptedTeamMember) {}
}
