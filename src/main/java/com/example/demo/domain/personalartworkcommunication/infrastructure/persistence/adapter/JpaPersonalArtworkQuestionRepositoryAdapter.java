package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkQuestionRepositoryAdapter
    implements PersonalArtworkQuestionRepository {

  private final PersonalArtworkQuestionJpaRepository personalArtworkQuestionJpaRepository;

  @Override
  public PersonalArtworkQuestion save(PersonalArtworkQuestion personalArtworkQuestion) {
    return personalArtworkQuestionJpaRepository.save(personalArtworkQuestion);
  }

  @Override
  public Optional<PersonalArtworkQuestion> findById(Long personalQuestionId) {
    return personalArtworkQuestionJpaRepository.findById(personalQuestionId);
  }

  @Override
  public Optional<PersonalArtworkQuestion> findActiveById(Long personalQuestionId) {
    return personalArtworkQuestionJpaRepository.findByPersonalQuestionIdAndDeletedAtIsNull(
        personalQuestionId);
  }

  @Override
  public List<PersonalArtworkQuestion> findActiveByPersonalArtworkIdWithCursor(
      Long personalArtworkId, Long cursorId, int limit) {
    return personalArtworkQuestionJpaRepository.findActiveByPersonalArtworkIdWithCursor(
        personalArtworkId, cursorId, PageRequest.of(0, limit));
  }
}
