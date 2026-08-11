package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.SpringDataArtworkQuestionJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkQuestionRepositoryAdapter implements ArtworkQuestionRepository {

  private final SpringDataArtworkQuestionJpaRepository artworkQuestionJpaRepository;

  @Override
  public ArtworkQuestion save(ArtworkQuestion artworkQuestion) {
    return artworkQuestionJpaRepository.save(artworkQuestion);
  }

  @Override
  public Optional<ArtworkQuestion> findById(Long questionId) {
    return artworkQuestionJpaRepository.findById(questionId);
  }

  @Override
  public Optional<ArtworkQuestion> findActiveByIdForUpdate(Long questionId) {
    return artworkQuestionJpaRepository.findActiveByIdForUpdate(questionId);
  }

  @Override
  public List<ArtworkQuestion> findActiveByDisplayArtworkIdWithCursor(
      Long displayArtworkId, Long cursorId, int limit) {
    return artworkQuestionJpaRepository.findActiveByDisplayArtworkIdWithCursor(
        displayArtworkId, cursorId, PageRequest.of(0, limit));
  }
}
