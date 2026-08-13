package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.repository.TeamMemberRepository;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataTeamMemberJpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaTeamMemberRepositoryAdapter implements TeamMemberRepository {

  private final SpringDataTeamMemberJpaRepository jpaRepository;

  public JpaTeamMemberRepositoryAdapter(SpringDataTeamMemberJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public boolean existsAcceptedByDisplayIdAndUserId(Long displayId, Long userId) {
    return jpaRepository.existsByDisplayIdAndUserIdValueAndAcceptedTrueAndDeletedAtIsNull(
        displayId, userId);
  }

  @Override
  public Optional<TeamMember> findAcceptedByDisplayIdAndUserId(Long displayId, Long userId) {
    return findActiveAcceptedByDisplayIdAndUserId(displayId, userId);
  }

  @Override
  public Optional<TeamMember> findActiveAcceptedByDisplayIdAndUserId(Long displayId, Long userId) {
    return jpaRepository.findByDisplayIdAndUserIdValueAndAcceptedTrueAndDeletedAtIsNull(
        displayId, userId);
  }

  @Override
  public List<TeamMember> findAcceptedLeadersByDisplayIds(Collection<Long> displayIds) {
    if (displayIds.isEmpty()) {
      return List.of();
    }
    return jpaRepository.findByDisplayIdInAndRoleAndAcceptedTrueAndDeletedAtIsNull(
        displayIds, TeamMemberRole.TEAM_LEADER);
  }

  @Override
  public TeamMember save(TeamMember teamMember) {
    return jpaRepository.save(teamMember);
  }
}
