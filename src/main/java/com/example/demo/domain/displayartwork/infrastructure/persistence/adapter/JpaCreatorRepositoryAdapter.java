package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import com.example.demo.domain.displayartwork.infrastructure.persistence.SpringDataCreatorJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaCreatorRepositoryAdapter implements CreatorRepository {

  private final SpringDataCreatorJpaRepository jpaRepository;

  public JpaCreatorRepositoryAdapter(SpringDataCreatorJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<Creator> findByDisplayArtworkId(Long displayArtworkId) {
    return jpaRepository.findByDisplayArtworkId(displayArtworkId);
  }

  @Override
  public Optional<Creator> findLeaderByDisplayArtworkId(Long displayArtworkId) {
    return jpaRepository.findFirstByDisplayArtworkIdAndIsLeaderTrue(displayArtworkId);
  }

  @Override
  public List<Creator> findLeadersByDisplayArtworkIds(List<Long> displayArtworkIds) {
    return jpaRepository.findByDisplayArtworkIdInAndIsLeaderTrue(displayArtworkIds);
  }

  @Override
  public Creator save(Creator creator) {
    return jpaRepository.save(creator);
  }

  @Override
  public List<Creator> saveAll(List<Creator> creators) {
    return jpaRepository.saveAll(creators);
  }

  @Override
  public void deleteAllByDisplayArtworkId(Long displayArtworkId) {
    jpaRepository.deleteAllByDisplayArtworkId(displayArtworkId);
  }
}
