package com.example.demo.domain.user.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import org.junit.jupiter.api.Test;

class ProfileImageUrlTest {

  @Test
  void normalizesBlankValueToNull() {
    assertThat(ProfileImageUrl.ofNullable("  ").value()).isNull();
  }

  @Test
  void acceptsUrlStartingWithCdnPrefix() {
    assertThat(
            ProfileImageUrl.ofNullable(
                    "https://d1tdgnysscm2va.cloudfront.net/images/user/profile.jpg")
                .value())
        .isEqualTo("https://d1tdgnysscm2va.cloudfront.net/images/user/profile.jpg");
  }

  @Test
  void rejectsUrlNotStartingWithCdnPrefix() {
    assertThatExceptionOfType(UserException.class)
        .isThrownBy(() -> ProfileImageUrl.ofNullable("https://cdn.example.com/profile.jpg"))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(UserErrorCode.INVALID_PROFILE_IMAGE_URL));
  }

  @Test
  void trimsValueBeforeValidatingCdnPrefix() {
    assertThat(
            ProfileImageUrl.ofNullable(
                    "  https://d1tdgnysscm2va.cloudfront.net/images/user/profile.jpg  ")
                .value())
        .isEqualTo("https://d1tdgnysscm2va.cloudfront.net/images/user/profile.jpg");
  }
}
