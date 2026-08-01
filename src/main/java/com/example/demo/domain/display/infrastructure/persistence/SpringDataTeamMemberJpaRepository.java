package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.domain.entity.TeamMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTeamMemberJpaRepository extends JpaRepository<TeamMember, Long> {

  boolean existsByDisplayIdAndUserIdValueAndAcceptedTrue(Long displayId, Long userId);

  Optional<TeamMember> findByDisplayIdAndUserIdValueAndAcceptedTrue(Long displayId, Long userId);
}
