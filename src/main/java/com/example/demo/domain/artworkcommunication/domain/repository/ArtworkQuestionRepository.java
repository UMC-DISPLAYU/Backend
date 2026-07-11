package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;

public interface ArtworkQuestionRepository {
    ArtworkQuestion save(ArtworkQuestion artworkQuestion);
}
