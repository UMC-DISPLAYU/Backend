package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "ArtworkFeeling")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtworkFeelingJpaEntity extends SoftDeleteBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feelingId", nullable = false)
    private Long feelingId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "displayArtworkId", nullable = false)
    private Long displayArtworkId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    private ArtworkFeelingJpaEntity(
            Long feelingId,
            String content,
            Long displayArtworkId,
            Long userId
    ) {
        this.feelingId = feelingId;
        this.content = content;
        this.displayArtworkId = displayArtworkId;
        this.userId = userId;
    }

    public static ArtworkFeelingJpaEntity of(
            Long feelingId,
            String content,
            Long displayArtworkId,
            Long userId
    ) {
        return new ArtworkFeelingJpaEntity(feelingId, content, displayArtworkId, userId);
    }
}
