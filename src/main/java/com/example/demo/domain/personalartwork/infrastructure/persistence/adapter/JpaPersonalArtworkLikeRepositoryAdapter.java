package com.example.demo.domain.personalartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkLike;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkLikeRepository;
import com.example.demo.domain.personalartwork.infrastructure.persistence.SpringDataPersonalArtworkLikeJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPersonalArtworkLikeRepositoryAdapter implements PersonalArtworkLikeRepository {

  private final SpringDataPersonalArtworkLikeJpaRepository jpaRepository;

  public JpaPersonalArtworkLikeRepositoryAdapter(
      SpringDataPersonalArtworkLikeJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<PersonalArtworkLike> findByPersonalArtworkIdAndUserId(
      Long personalArtworkId, Long userId) {
    return jpaRepository.findByPersonalArtworkIdAndUserId(personalArtworkId, userId);
  }

  @Override
  public PersonalArtworkLike save(PersonalArtworkLike personalArtworkLike) {
    return jpaRepository.save(personalArtworkLike);
  }

  @Override
  public int deleteByPersonalArtworkIdAndUserId(Long personalArtworkId, Long userId) {
    return jpaRepository.deleteByPersonalArtworkIdAndUserId(personalArtworkId, userId);
  }

  @Override
  public long countByPersonalArtworkId(Long personalArtworkId) {
    return jpaRepository.countByPersonalArtworkId(personalArtworkId);
  }
}
