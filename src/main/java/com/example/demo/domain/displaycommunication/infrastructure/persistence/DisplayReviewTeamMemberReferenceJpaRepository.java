package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayReviewTeamMemberReferenceJpaRepository
    extends JpaRepository<DisplayReviewTeamMemberReferenceJpaEntity, Long> {
  boolean existsByDisplayIdAndUserIdAndAcceptedTrue(Long displayId, Long userId);
}
