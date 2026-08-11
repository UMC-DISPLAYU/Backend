package com.example.demo.domain.artist.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;
import com.example.demo.domain.user.domain.aggregate.User;
import org.junit.jupiter.api.Test;

class ArtistProfileTest {

  private final User user = User.builder().id(1L).nickname("artist").build();

  @Test
  void updatesProfileWhenStateIsValid() {
    ArtistProfile profile = createProfile();

    profile.updateProfile("introduction", "https://portfolio.example.com", "university");

    assertThat(profile.getIntroduction()).isEqualTo("introduction");
    assertThat(profile.getPortfolioUrl()).isEqualTo("https://portfolio.example.com");
    assertThat(profile.getUnivName()).isEqualTo("university");
  }

  @Test
  void rejectsIntroductionLongerThanOneHundredCharacters() {
    ArtistProfile profile = createProfile();

    assertThatExceptionOfType(ArtistException.class)
        .isThrownBy(() -> profile.updateProfile("a".repeat(101), null, "university"))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(ArtistErrorCode.INVALID_INTRODUCTION));
  }

  @Test
  void rejectsInvalidPortfolioUrl() {
    ArtistProfile profile = createProfile();

    assertThatExceptionOfType(ArtistException.class)
        .isThrownBy(() -> profile.updateProfile(null, "not-a-url", "university"))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(ArtistErrorCode.INVALID_EXTERNAL_LINK));
  }

  private ArtistProfile createProfile() {
    return ArtistProfile.create(user, "artist", "artist@du.ac.kr", "university", null);
  }
}
