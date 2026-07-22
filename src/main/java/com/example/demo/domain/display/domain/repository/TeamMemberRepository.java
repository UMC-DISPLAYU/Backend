package com.example.demo.domain.display.domain.repository;

import com.example.demo.domain.display.domain.entity.TeamMember;

public interface TeamMemberRepository {

  boolean existsAcceptedByDisplayIdAndUserId(Long displayId, Long userId);

  TeamMember save(TeamMember teamMember);
}
