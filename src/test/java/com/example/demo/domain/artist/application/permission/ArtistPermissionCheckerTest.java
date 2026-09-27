package com.example.demo.domain.artist.application.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;
import com.example.demo.domain.artist.domain.model.ArtistUserQualification;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ArtistPermissionCheckerTest {

  private static final Instant UPDATED_AT = Instant.parse("2026-09-27T00:00:00Z");
  private final ArtistPermissionChecker checker = new ArtistPermissionChecker();

  @Test
  void requireProfileCreationEligibleAllowsActiveSchoolVerifiedUser() {
    ArtistUserQualification qualification = qualification(true, false);

    assertThatCode(() -> checker.requireProfileCreationEligible(qualification))
        .doesNotThrowAnyException();
  }

  @Test
  void requireProfileCreationEligibleRejectsIncompleteVerification() {
    ArtistUserQualification qualification = qualification(false, false);

    assertThatThrownBy(() -> checker.requireProfileCreationEligible(qualification))
        .isInstanceOfSatisfying(
            ArtistException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArtistErrorCode.ARTIST_PROFILE_REQUIRES_VERIFIED_USER));
  }

  @Test
  void requireVerifiedRejectsWithdrawnUser() {
    ArtistUserQualification qualification = qualification(true, true);

    assertThatThrownBy(() -> checker.requireVerified(qualification))
        .isInstanceOfSatisfying(
            ArtistException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArtistErrorCode.ARTIST_VERIFICATION_REQUIRED));
  }

  private ArtistUserQualification qualification(boolean schoolVerified, boolean withdrawn) {
    return ArtistUserQualification.restore(
        1L,
        schoolVerified ? "student@du.ac.kr" : null,
        schoolVerified ? "덕성여자대학교" : null,
        schoolVerified,
        withdrawn,
        1L,
        null,
        UPDATED_AT);
  }
}
