package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkFeelingJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkFeelingRepositoryAdapter
    implements PersonalArtworkFeelingRepository {

  private final SpringDataPersonalArtworkFeelingJpaRepository personalArtworkFeelingJpaRepository;

  @Override
  public PersonalArtworkFeeling save(PersonalArtworkFeeling personalArtoworkFeeling) {
    return personalArtworkFeelingJpaRepository.save(personalArtoworkFeeling);
  }

  @Override
  public Optional<PersonalArtworkFeeling> findById(Long personalFeelingId) {
    return personalArtworkFeelingJpaRepository.findById(personalFeelingId);
  }

  @Override
  public List<PersonalArtworkFeeling> findByPersonalArtworkIdWithCursorIncludingDeleted(
      Long personalArtworkId, Long cursorId, int limit) {
    return personalArtworkFeelingJpaRepository.findByPersonalArtworkIdWithCursorIncludingDeleted(
        personalArtworkId, cursorId, PageRequest.of(0, limit));
  }
}
