package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.domain.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTeamMemberJpaRepository extends JpaRepository<TeamMember, Long> {

  boolean existsByDisplayIdAndUserIdValueAndAcceptedTrue(Long displayId, Long userId);
}
