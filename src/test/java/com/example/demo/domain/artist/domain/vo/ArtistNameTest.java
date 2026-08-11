package com.example.demo.domain.artist.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;
import org.junit.jupiter.api.Test;

class ArtistNameTest {

  @Test
  void acceptsKoreanEnglishAndNumbersBetweenTwoAndFifteenCharacters() {
    assertThat(ArtistName.of("가1").value()).isEqualTo("가1");
    assertThat(ArtistName.of("Artist123456789").value()).hasSize(15);
  }

  @Test
  void rejectsArtistNameShorterThanTwoCharacters() {
    assertInvalid("가");
  }

  @Test
  void rejectsArtistNameLongerThanFifteenCharacters() {
    assertInvalid("a".repeat(16));
  }

  @Test
  void rejectsWhitespaceAndSpecialCharacters() {
    assertInvalid("작가 이름");
    assertInvalid("artist!");
  }

  private void assertInvalid(String value) {
    assertThatExceptionOfType(ArtistException.class)
        .isThrownBy(() -> ArtistName.of(value))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(ArtistErrorCode.INVALID_ARTIST_NAME));
  }
}
