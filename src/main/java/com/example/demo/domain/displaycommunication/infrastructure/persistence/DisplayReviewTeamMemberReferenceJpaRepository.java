package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayReviewTeamMemberReferenceJpaRepository
    extends JpaRepository<DisplayReviewTeamMemberReferenceJpaEntity, Long> {
  boolean existsByDisplayIdAndUserIdAndAcceptedTrue(Long displayId, Long userId);

  List<DisplayReviewTeamMemberReferenceJpaEntity> findByDisplayIdAndAcceptedTrue(Long displayId);
}
