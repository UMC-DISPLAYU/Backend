package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkQuestionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkQuestionRepositoryAdapter implements ArtworkQuestionRepository {

    private final ArtworkQuestionJpaRepository artworkQuestionJpaRepository;

    @Override
    public ArtworkQuestion save(ArtworkQuestion artworkQuestion) {
        return artworkQuestionJpaRepository.save(artworkQuestion);
    }
}
