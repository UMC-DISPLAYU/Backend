package com.example.demo.domain.personalartwork.domain.repository;

import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkLike;
import java.util.Optional;

public interface PersonalArtworkLikeRepository {

  Optional<PersonalArtworkLike> findByPersonalArtworkIdAndUserId(
      Long personalArtworkId, Long userId);

  PersonalArtworkLike save(PersonalArtworkLike personalArtworkLike);

  int deleteByPersonalArtworkIdAndUserId(Long personalArtworkId, Long userId);

  long countByPersonalArtworkId(Long personalArtworkId);
}
