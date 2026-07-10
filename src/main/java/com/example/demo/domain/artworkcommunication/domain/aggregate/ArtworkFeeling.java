package com.example.demo.domain.artworkcommunication.domain.aggregate;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ArtworkFeeling {

    private Long feelingId;
    private Long displayArtworkId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private ArtworkFeeling(
            Long feelingId,
            Long displayArtworkId,
            Long userId,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        this.feelingId = feelingId;
        this.displayArtworkId = displayArtworkId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static ArtworkFeeling create(
            Long displayArtworkId,
            Long userId,
            String content
    ) {
        LocalDateTime now = LocalDateTime.now();

        return new ArtworkFeeling(
                null,
                displayArtworkId,
                userId,
                content,
                now,
                now,
                null
        );
    }

    public static ArtworkFeeling of(
            Long feelingId,
            Long displayArtworkId,
            Long userId,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        return new ArtworkFeeling(
                feelingId,
                displayArtworkId,
                userId,
                content,
                createdAt,
                updatedAt,
                deletedAt
        );
    }
}
