package com.example.demo.domain.personalartwork.infrastructure.persistence;

import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataPersonalArtworkLikeJpaRepository
    extends JpaRepository<PersonalArtworkLike, Long> {

  Optional<PersonalArtworkLike> findByPersonalArtworkIdAndUserId(
      Long personalArtworkId, Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      DELETE FROM PersonalArtworkLike artworkLike
      WHERE artworkLike.personalArtworkId = :personalArtworkId
        AND artworkLike.userId = :userId
      """)
  int deleteByPersonalArtworkIdAndUserId(
      @Param("personalArtworkId") Long personalArtworkId, @Param("userId") Long userId);

  long countByPersonalArtworkId(Long personalArtworkId);
}
