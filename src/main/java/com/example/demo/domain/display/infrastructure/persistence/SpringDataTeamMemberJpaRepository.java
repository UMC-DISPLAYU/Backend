package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataTeamMemberJpaRepository extends JpaRepository<TeamMember, Long> {

  boolean existsByDisplayIdAndUserIdValueAndAcceptedTrueAndDeletedAtIsNull(
      Long displayId, Long userId);

  Optional<TeamMember> findByDisplayIdAndUserIdValueAndAcceptedTrueAndDeletedAtIsNull(
      Long displayId, Long userId);

  @Query(
      """
      SELECT CASE WHEN COUNT(teamMember) > 0 THEN true ELSE false END
      FROM TeamMember teamMember
      WHERE teamMember.userId.value = :userId
        AND teamMember.role = :role
        AND teamMember.accepted = true
        AND teamMember.deletedAt IS NULL
        AND teamMember.display.deletedAt IS NULL
      """)
  boolean existsActiveAcceptedByUserIdAndRole(
      @Param("userId") Long userId, @Param("role") TeamMemberRole role);

  List<TeamMember> findByDisplayIdInAndRoleAndAcceptedTrueAndDeletedAtIsNull(
      Collection<Long> displayIds, TeamMemberRole role);
}
