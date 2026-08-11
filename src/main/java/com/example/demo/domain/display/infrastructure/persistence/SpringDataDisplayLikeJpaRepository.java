package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.domain.entity.DisplayLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataDisplayLikeJpaRepository extends JpaRepository<DisplayLike, Long> {

  Optional<DisplayLike> findByDisplayIdAndUserIdValue(Long displayId, Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      DELETE FROM DisplayLike displayLike
      WHERE displayLike.displayId = :displayId
        AND displayLike.userId.value = :userId
      """)
  int deleteByDisplayIdAndUserId(@Param("displayId") Long displayId, @Param("userId") Long userId);

  long countByDisplayId(Long displayId);
}
