package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTeamMemberJpaRepository extends JpaRepository<TeamMember, Long> {

  boolean existsByDisplayIdAndUserIdValueAndAcceptedTrueAndDeletedAtIsNull(
      Long displayId, Long userId);

  Optional<TeamMember> findByDisplayIdAndUserIdValueAndAcceptedTrueAndDeletedAtIsNull(
      Long displayId, Long userId);

  List<TeamMember> findByDisplayIdInAndRoleAndAcceptedTrueAndDeletedAtIsNull(
      Collection<Long> displayIds, TeamMemberRole role);
}
