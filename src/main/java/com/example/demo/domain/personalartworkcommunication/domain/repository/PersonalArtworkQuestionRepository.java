package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;

public interface PersonalArtworkQuestionRepository {
  PersonalArtworkQuestion save(PersonalArtworkQuestion personalArtworkQuestion);
}
