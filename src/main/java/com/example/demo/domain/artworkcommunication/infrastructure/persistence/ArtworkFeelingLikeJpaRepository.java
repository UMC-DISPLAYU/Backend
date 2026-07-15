package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkFeelingLikeJpaRepository extends JpaRepository<ArtworkFeelingLike, Long> {

  Optional<ArtworkFeelingLike> findByFeelingIdAndUserId(Long feelingId, Long userId);

  long countByFeelingIdAndDeletedAtIsNull(Long feelingId);
}
