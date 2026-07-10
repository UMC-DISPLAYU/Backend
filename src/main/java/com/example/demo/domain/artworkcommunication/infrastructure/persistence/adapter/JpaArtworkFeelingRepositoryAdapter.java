package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingJpaEntity;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingJpaRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkFeelingRepositoryAdapter implements ArtworkFeelingRepository {

    private final ArtworkFeelingJpaRepository artworkFeelingJpaRepository;

    @Override
    public ArtworkFeeling save(ArtworkFeeling artworkFeeling) {
        ArtworkFeelingJpaEntity entity =
                ArtworkFeelingPersistenceMapper.toJpaEntity(artworkFeeling);

        ArtworkFeelingJpaEntity savedEntity =
                artworkFeelingJpaRepository.save(entity);

        return ArtworkFeelingPersistenceMapper.toDomain(savedEntity);
    }
}
