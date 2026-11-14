package com.example.demo.domain.display.domain.repository;

import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository {

  boolean existsAcceptedByDisplayIdAndUserId(Long displayId, Long userId);

  Optional<TeamMember> findAcceptedByDisplayIdAndUserId(Long displayId, Long userId);

  Optional<TeamMember> findActiveAcceptedByDisplayIdAndUserId(Long displayId, Long userId);

  boolean existsActiveAcceptedByUserIdAndRole(Long userId, TeamMemberRole role);

  List<TeamMember> findAcceptedLeadersByDisplayIds(Collection<Long> displayIds);

  TeamMember save(TeamMember teamMember);
}
