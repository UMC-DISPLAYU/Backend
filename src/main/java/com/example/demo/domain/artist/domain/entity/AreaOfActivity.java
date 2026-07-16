package com.example.demo.domain.artist.domain.entity;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "AreaOfActivity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AreaOfActivity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "areaOfActivityId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artistProfileId", nullable = false)
    private ArtistProfile artistProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "field", nullable = false)
    private ActivityCategory field;

    private AreaOfActivity(
            ArtistProfile artistProfile,
            ActivityCategory field
    ) {
        this.artistProfile = artistProfile;
        this.field = field;
    }

    public static AreaOfActivity create(
            ArtistProfile artistProfile,
            ActivityCategory field
    ) {
        return new AreaOfActivity(
                artistProfile,
                field
        );
    }

    public void updateField(ActivityCategory field) {
        this.field = field;
    }
}
