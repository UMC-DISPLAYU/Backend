package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import java.util.List;
import java.util.Optional;

public interface PersonalArtworkQuestionRepository {
  PersonalArtworkQuestion save(PersonalArtworkQuestion personalArtworkQuestion);

  Optional<PersonalArtworkQuestion> findById(Long personalArtworkQuestionId);

  Optional<PersonalArtworkQuestion> findActiveById(Long personalArtworkQuestionId);

  Optional<PersonalArtworkQuestion> findActiveByIdForUpdate(Long personalArtworkQuestionId);

  List<PersonalArtworkQuestion> findActiveByPersonalArtworkIdWithCursor(
      Long personalArtworkId, Long cursorId, int limit);
}
