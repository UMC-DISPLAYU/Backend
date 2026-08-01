package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.PreviewFilterType;
import com.example.demo.domain.displayartwork.infrastructure.persistence.SpringDataDisplayArtworkJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayArtworkRepositoryAdapter implements DisplayArtworkRepository {

  private final SpringDataDisplayArtworkJpaRepository jpaRepository;

  public JpaDisplayArtworkRepositoryAdapter(SpringDataDisplayArtworkJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<DisplayArtwork> findById(Long displayArtworkId) {
    return jpaRepository.findById(displayArtworkId);
  }

  @Override
  public int countByDisplayId(Long displayId) {
    return jpaRepository.countByDisplayId(displayId);
  }

  @Override
  public Optional<Integer> findMaxWorkSortOrderByDisplayId(Long displayId) {
    return jpaRepository.findMaxWorkSortOrderByDisplayId(displayId);
  }

  @Override
  public List<DisplayArtwork> findAllByDisplayId(Long displayId) {
    return jpaRepository.findAllByDisplayId(displayId);
  }

  @Override
  public List<DisplayArtwork> findAllByParticipantUserId(Long userId) {
    return jpaRepository.findAllByParticipantUserId(userId);
  }

  @Override
  public List<DisplayArtwork> findPreview(
      PreviewFilterType type, ArtworkType field, String school, int page, int size) {
    boolean requireGraduation = type == PreviewFilterType.GRADUATION;
    return jpaRepository.findPreview(
        requireGraduation, field, school, PageRequest.of(page, size + 1));
  }

  @Override
  public DisplayArtwork save(DisplayArtwork displayArtwork) {
    return jpaRepository.save(displayArtwork);
  }
}
