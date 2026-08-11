package com.example.demo.domain.displayartwork.infrastructure.persistence;

import com.example.demo.domain.displayartwork.domain.entity.DisplayArtworkLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataDisplayArtworkLikeJpaRepository
    extends JpaRepository<DisplayArtworkLike, Long> {

  Optional<DisplayArtworkLike> findByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      DELETE FROM DisplayArtworkLike artworkLike
      WHERE artworkLike.displayArtworkId = :displayArtworkId
        AND artworkLike.userId = :userId
      """)
  int deleteByDisplayArtworkIdAndUserId(
      @Param("displayArtworkId") Long displayArtworkId, @Param("userId") Long userId);

  long countByDisplayArtworkId(Long displayArtworkId);

  boolean existsByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId);
}
