package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkFeelingRepositoryAdapter
    implements PersonalArtworkFeelingRepository {

  private final PersonalArtworkFeelingJpaRepository personalArtworkFeelingJpaRepository;

  @Override
  public PersonalArtworkFeeling save(PersonalArtworkFeeling personalArtoworkFeeling) {
    return personalArtworkFeelingJpaRepository.save(personalArtoworkFeeling);
  }
}
