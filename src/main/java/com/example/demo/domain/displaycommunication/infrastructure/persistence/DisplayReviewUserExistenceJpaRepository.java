package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayReviewUserExistenceJpaRepository
    extends JpaRepository<DisplayReviewUserReferenceJpaEntity, Long> {
  boolean existsByUserIdAndDeletedAtIsNull(Long userId);

  Optional<DisplayReviewUserReferenceJpaEntity> findByUserIdAndDeletedAtIsNull(Long userId);

  List<DisplayReviewUserReferenceJpaEntity> findByUserIdInAndDeletedAtIsNull(Set<Long> userIds);
}
