package com.example.demo.domain.artworkcommunication.domain.aggregate;

import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "ArtworkFeeling")
public class ArtworkFeeling extends SoftDeleteBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feelingId")
    private Long feelingId;

    @Column(name = "displayArtworkId", nullable = false)
    private Long displayArtworkId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    protected ArtworkFeeling() {}

    private ArtworkFeeling(
            Long feelingId,
            Long displayArtworkId,
            Long userId,
            String content
    ) {
        this.feelingId = feelingId;
        this.displayArtworkId = displayArtworkId;
        this.userId = userId;
        this.content = content;
    }

    public static ArtworkFeeling create(
            Long displayArtworkId,
            Long userId,
            String content
    ) {
        return new ArtworkFeeling(
                null,
                displayArtworkId,
                userId,
                content
        );
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public boolean isWrittenBy(Long userId) {
        return this.userId.equals(userId);
    }

    public boolean belongsToArtwork(Long displayArtworkId) {
        return this.displayArtworkId.equals(displayArtworkId);
    }
}
