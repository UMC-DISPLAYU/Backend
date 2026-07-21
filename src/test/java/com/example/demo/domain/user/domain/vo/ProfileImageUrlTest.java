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
  void acceptsHttpAndHttpsUrls() {
    assertThat(ProfileImageUrl.ofNullable("https://cdn.example.com/profile.jpg").value())
        .isEqualTo("https://cdn.example.com/profile.jpg");
    assertThat(ProfileImageUrl.ofNullable("http://cdn.example.com/profile.jpg").value())
        .isEqualTo("http://cdn.example.com/profile.jpg");
  }

  @Test
  void rejectsInvalidUrl() {
    assertThatExceptionOfType(UserException.class)
        .isThrownBy(() -> ProfileImageUrl.ofNullable("not-a-url"))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(UserErrorCode.INVALID_PROFILE_IMAGE_URL));
  }
}
