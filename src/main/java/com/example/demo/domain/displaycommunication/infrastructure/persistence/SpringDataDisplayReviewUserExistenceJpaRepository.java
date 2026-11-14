package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataDisplayReviewUserExistenceJpaRepository
    extends JpaRepository<DisplayReviewUserReferenceJpaEntity, Long> {
  boolean existsByUserIdAndDeletedAtIsNull(Long userId);

  Optional<DisplayReviewUserReferenceJpaEntity> findByUserIdAndDeletedAtIsNull(Long userId);

  List<DisplayReviewUserReferenceJpaEntity> findByUserIdInAndDeletedAtIsNull(Set<Long> userIds);

  @Query("select user from DisplayReviewUserReferenceJpaEntity user where user.userId in :userIds")
  List<DisplayReviewUserReferenceJpaEntity> findIncludingWithdrawnByUserIdIn(
      @Param("userIds") Set<Long> userIds);
}
