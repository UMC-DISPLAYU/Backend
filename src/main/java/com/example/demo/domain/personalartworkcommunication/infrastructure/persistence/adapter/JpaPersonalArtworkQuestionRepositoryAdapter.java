package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
}
