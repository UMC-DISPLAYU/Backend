package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkQuestionJpaRepository extends JpaRepository<ArtworkQuestion, Long> {
}
