package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;

public class ArtworkFeelingPersistenceMapper {
    public static ArtworkFeelingJpaEntity toJpaEntity(ArtworkFeeling domain) {
        return ArtworkFeelingJpaEntity.of(
                domain.getFeelingId(),
                domain.getContent(),
                domain.getDisplayArtworkId(),
                domain.getUserId()
        );
    }

    public static ArtworkFeeling toDomain(ArtworkFeelingJpaEntity entity) {
        return ArtworkFeeling.of(
                entity.getFeelingId(),
                entity.getDisplayArtworkId(),
                entity.getUserId(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
