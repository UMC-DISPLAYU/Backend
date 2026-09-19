package com.example.demo.domain.display.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDisplayDeletionCleanupFailureJpaRepository
    extends JpaRepository<DisplayDeletionCleanupFailure, Long> {

  List<DisplayDeletionCleanupFailure> findTop100ByRecoveredAtIsNullAndIdGreaterThanOrderByIdAsc(
      Long id);
}
