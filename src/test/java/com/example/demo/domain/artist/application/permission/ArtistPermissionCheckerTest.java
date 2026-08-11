package com.example.demo.domain.artist.application.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import org.junit.jupiter.api.Test;

class ArtistPermissionCheckerTest {

  private final ArtistPermissionChecker checker = new ArtistPermissionChecker();

  @Test
  void requireProfileCreationEligibleAllowsVerifiedUserWithSchoolInformation() {
    User user = verifiedUser();

    assertThatCode(() -> checker.requireProfileCreationEligible(user)).doesNotThrowAnyException();
  }

  @Test
  void requireProfileCreationEligibleRejectsIncompleteVerificationWithExistingErrorCode() {
    User user = User.builder().isVerified(true).schoolEmail("student@du.ac.kr").build();

    assertThatThrownBy(() -> checker.requireProfileCreationEligible(user))
        .isInstanceOfSatisfying(
            ArtistException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArtistErrorCode.ARTIST_PROFILE_REQUIRES_VERIFIED_USER));
  }

  @Test
  void requireVerifiedRejectsUnverifiedUserWithExistingErrorCode() {
    User user = User.builder().isVerified(false).build();

    assertThatThrownBy(() -> checker.requireVerified(user))
        .isInstanceOfSatisfying(
            UserException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(UserErrorCode.ARTIST_VERIFICATION_REQUIRED));
  }

  private User verifiedUser() {
    return User.builder()
        .isVerified(true)
        .schoolEmail("student@du.ac.kr")
        .univName("디유대학교")
        .build();
  }
}
