package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import java.util.List;
import java.util.Optional;

public interface PersonalArtworkFeelingRepository {
  PersonalArtworkFeeling save(PersonalArtworkFeeling personalArtworkFeeling);

  Optional<PersonalArtworkFeeling> findById(Long feelingId);

  List<PersonalArtworkFeeling> findByPersonalArtworkIdWithCursorIncludingDeleted(
      Long personalArtworkId, Long cursorId, int limit);
}
