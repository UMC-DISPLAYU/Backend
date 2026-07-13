package com.example.demo.domain.personalartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import com.example.demo.domain.personalartwork.infrastructure.persistence.SpringDataPersonalArtworkJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPersonalArtworkRepositoryAdapter implements PersonalArtworkRepository {

  private final SpringDataPersonalArtworkJpaRepository jpaRepository;

  public JpaPersonalArtworkRepositoryAdapter(SpringDataPersonalArtworkJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<PersonalArtwork> findById(Long personalArtworkId) {
    return jpaRepository.findById(personalArtworkId);
  }

  @Override
  public List<PersonalArtwork> findAllByOwnerUserIdOrderByCreatedAtAsc(Long userId) {
    return jpaRepository.findAllByOwnerUserIdOrderByCreatedAtAsc(userId);
  }

  @Override
  public PersonalArtwork save(PersonalArtwork personalArtwork) {
    return jpaRepository.save(personalArtwork);
  }
}
