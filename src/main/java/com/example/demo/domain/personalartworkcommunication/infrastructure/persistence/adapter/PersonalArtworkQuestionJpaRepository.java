package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalArtworkQuestionJpaRepository
    extends JpaRepository<PersonalArtworkQuestion, Long> {}
