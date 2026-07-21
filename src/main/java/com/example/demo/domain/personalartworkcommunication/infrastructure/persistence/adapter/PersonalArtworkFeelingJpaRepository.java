package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalArtworkFeelingJpaRepository
    extends JpaRepository<PersonalArtworkFeeling, Long> {}
