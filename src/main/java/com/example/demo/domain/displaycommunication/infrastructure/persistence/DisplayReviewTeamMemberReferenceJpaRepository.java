package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisplayReviewTeamMemberReferenceJpaRepository
    extends JpaRepository<DisplayReviewTeamMemberReferenceJpaEntity, Long> {
  boolean existsByDisplayIdAndUserIdAndAcceptedTrue(Long displayId, Long userId);

  @Query(
      """
      SELECT teamMember.userId
      FROM DisplayReviewTeamMemberReferenceJpaEntity teamMember
      WHERE teamMember.displayId = :displayId
        AND teamMember.accepted = true
      """)
  Set<Long> findAcceptedUserIdsByDisplayId(@Param("displayId") Long displayId);
}
