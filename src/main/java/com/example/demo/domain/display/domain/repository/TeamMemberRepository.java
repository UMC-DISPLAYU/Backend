package com.example.demo.domain.display.domain.repository;

import com.example.demo.domain.display.domain.entity.TeamMember;
import java.util.Optional;

public interface TeamMemberRepository {

  boolean existsAcceptedByDisplayIdAndUserId(Long displayId, Long userId);

  Optional<TeamMember> findAcceptedByDisplayIdAndUserId(Long displayId, Long userId);

  Optional<TeamMember> findActiveAcceptedByDisplayIdAndUserId(Long displayId, Long userId);

  TeamMember save(TeamMember teamMember);
}
