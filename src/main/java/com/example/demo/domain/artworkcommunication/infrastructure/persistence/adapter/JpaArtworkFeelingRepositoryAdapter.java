package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkFeelingRepositoryAdapter implements ArtworkFeelingRepository {

  private final ArtworkFeelingJpaRepository artworkFeelingJpaRepository;

  @Override
  public ArtworkFeeling save(ArtworkFeeling artworkFeeling) {
    return artworkFeelingJpaRepository.save(artworkFeeling);
  }

  @Override
  public Optional<ArtworkFeeling> findById(Long feelingId) {
    return artworkFeelingJpaRepository.findById(feelingId);
  }

  @Override
  public List<ArtworkFeeling> findActiveByDisplayArtworkId(Long displayArtworkId) {
    return artworkFeelingJpaRepository.findByDisplayArtworkIdAndDeletedAtIsNullOrderByCreatedAtAsc(
        displayArtworkId);
  }

  @Override
  public List<ArtworkFeeling> findByDisplayArtworkIdWithCursorIncludingDeleted(
      Long displayArtworkId, Long cursorId, int limit) {
    return artworkFeelingJpaRepository.findByDisplayArtworkIdWithCursorIncludingDeleted(
        displayArtworkId, cursorId, PageRequest.of(0, limit));
  }
}
