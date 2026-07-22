package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import java.util.Optional;

public interface PersonalArtworkFeelingRepository {
  PersonalArtworkFeeling save(PersonalArtworkFeeling personalArtworkFeeling);

  Optional<PersonalArtworkFeeling> findById(Long id);
}
