package com.example.demo.domain.artist.domain.aggregate;



import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "ArtistProfile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistProfile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artistProfileId")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "artistName", nullable = false, length = 50)
    private String artistName;

    @Column(name = "schoolEmail", nullable = false)
    private String schoolEmail;

    @Column(name = "univName", nullable = false)
    private String univName;

    @Column(name = "portfolioUrl")
    private String portfolioUrl;

    private ArtistProfile(
            User user,
            String artistName,
            String schoolEmail,
            String univName,
            String portfolioUrl
    ) {
        this.user = user;
        this.artistName = artistName;
        this.schoolEmail = schoolEmail;
        this.univName = univName;
        this.portfolioUrl = portfolioUrl;
    }

    public static ArtistProfile create(
            User user,
            String artistName,
            String schoolEmail,
            String univName,
            String portfolioUrl
    ) {
        return new ArtistProfile(
                user,
                artistName,
                schoolEmail,
                univName,
                portfolioUrl
        );
    }

    public void updateArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void updatePortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }
}