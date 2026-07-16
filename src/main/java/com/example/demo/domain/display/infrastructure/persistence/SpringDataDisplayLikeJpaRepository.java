package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.domain.entity.DisplayLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDisplayLikeJpaRepository extends JpaRepository<DisplayLike, Long> {

  Optional<DisplayLike> findByDisplayIdAndUserIdValue(Long displayId, Long userId);

  long countByDisplayIdAndDeletedAtIsNull(Long displayId);
}
