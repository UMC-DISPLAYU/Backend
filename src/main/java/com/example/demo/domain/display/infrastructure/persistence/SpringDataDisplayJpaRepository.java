package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.domain.aggregate.Display;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface SpringDataDisplayJpaRepository extends JpaRepository<Display, Long> {

  @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
  Optional<Display> findWithOptimisticLockById(Long displayId);

  Optional<Display> findByInvitationToken(String invitationToken);
}
