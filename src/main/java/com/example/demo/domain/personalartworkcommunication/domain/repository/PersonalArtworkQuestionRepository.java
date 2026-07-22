package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import java.util.Optional;

public interface PersonalArtworkQuestionRepository {
  PersonalArtworkQuestion save(PersonalArtworkQuestion personalArtworkQuestion);

  Optional<PersonalArtworkQuestion> findById(Long personalArtworkQuestionId);
}
