package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.port.DisplayDeletionCleanupFailureRecorder;
import com.example.demo.domain.display.infrastructure.persistence.DisplayDeletionCleanupFailure;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayDeletionCleanupFailureJpaRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaDisplayDeletionCleanupFailureRecorder
    implements DisplayDeletionCleanupFailureRecorder {

  private final SpringDataDisplayDeletionCleanupFailureJpaRepository repository;

  public JpaDisplayDeletionCleanupFailureRecorder(
      SpringDataDisplayDeletionCleanupFailureJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void recordFailure(
      Long displayId, LocalDateTime deletedAt, int retryCount, RuntimeException exception) {
    repository.save(
        DisplayDeletionCleanupFailure.from(displayId, deletedAt, retryCount, exception));
  }
}
