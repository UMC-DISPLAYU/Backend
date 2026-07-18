package com.example.demo.domain.personalartwork.infrastructure.persistence;

import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPersonalArtworkLikeJpaRepository
    extends JpaRepository<PersonalArtworkLike, Long> {

  Optional<PersonalArtworkLike> findByPersonalArtworkIdAndUserId(
      Long personalArtworkId, Long userId);

  long countByPersonalArtworkIdAndDeletedAtIsNull(Long personalArtworkId);

  boolean existsByPersonalArtworkIdAndUserIdAndDeletedAtIsNull(Long personalArtworkId, Long userId);
}
