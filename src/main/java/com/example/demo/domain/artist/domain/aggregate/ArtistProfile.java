package com.example.demo.domain.artist.domain.aggregate;

import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;
import com.example.demo.domain.artist.domain.vo.ArtistName;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "ArtistProfile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistProfile extends BaseTimeEntity {

  private static final int INTRODUCTION_MAX_LENGTH = 100;
  private static final int EXTERNAL_URL_MAX_LENGTH = 255;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "artistProfileId")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @Column(name = "artistName", nullable = false, length = 15)
  private String artistName;

  @Column(name = "profileImageUrl", length = 2048)
  private String profileImageUrl;

  @Column(name = "schoolEmail", nullable = false)
  private String schoolEmail;

  @Column(name = "univName", nullable = false)
  private String univName;

  @Column(name = "portfolioUrl")
  private String portfolioUrl;

  @Column(name = "instagramUrl", length = 255)
  private String instagramUrl;

  @Column(name = "introduction", length = 100)
  private String introduction;

  private ArtistProfile(
      User user, String artistName, String schoolEmail, String univName, String portfolioUrl) {
    this.user = user;
    this.artistName = ArtistName.of(artistName).value();
    this.schoolEmail = schoolEmail;
    this.univName = univName;
    this.portfolioUrl = portfolioUrl;
  }

  public static ArtistProfile create(
      User user, String artistName, String schoolEmail, String univName, String portfolioUrl) {
    return new ArtistProfile(user, artistName, schoolEmail, univName, portfolioUrl);
  }

  public void updateArtistName(String artistName) {
    this.artistName = ArtistName.of(artistName).value();
  }

  public void updatePortfolioUrl(String portfolioUrl) {
    this.portfolioUrl = portfolioUrl;
  }

  public void updateProfileImage(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  public void updateProfile(
      String introduction, String portfolioUrl, String instagramUrl, String univName) {
    validateIntroduction(introduction);
    validateExternalUrl(portfolioUrl);
    validateExternalUrl(instagramUrl);
    this.introduction = introduction;
    this.portfolioUrl = portfolioUrl;
    this.instagramUrl = instagramUrl;
    this.univName = univName;
  }

  private void validateIntroduction(String introduction) {
    if (introduction != null && introduction.length() > INTRODUCTION_MAX_LENGTH) {
      throw new ArtistException(ArtistErrorCode.INVALID_INTRODUCTION);
    }
  }

  private void validateExternalUrl(String externalUrl) {
    if (externalUrl == null) {
      return;
    }
    if (externalUrl.length() > EXTERNAL_URL_MAX_LENGTH) {
      throw new ArtistException(ArtistErrorCode.INVALID_EXTERNAL_LINK);
    }
    try {
      URI uri = new URI(externalUrl);
      if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
          || uri.getHost() == null) {
        throw new ArtistException(ArtistErrorCode.INVALID_EXTERNAL_LINK);
      }
    } catch (URISyntaxException exception) {
      throw new ArtistException(ArtistErrorCode.INVALID_EXTERNAL_LINK);
    }
  }
}
