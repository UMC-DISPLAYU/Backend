package com.example.demo.domain.personalartwork.application.permission;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.error.PersonalArtworkErrorCode;
import com.example.demo.domain.personalartwork.domain.repository.ArtistVerificationRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import org.junit.jupiter.api.Test;

class PersonalArtworkPermissionCheckerTest {

  private final ArtistVerificationRepository artistVerificationRepository =
      mock(ArtistVerificationRepository.class);
  private final PersonalArtworkPermissionChecker permissionChecker =
      new PersonalArtworkPermissionChecker(artistVerificationRepository);

  @Test
  void verifiedArtistIsAllowed() {
    when(artistVerificationRepository.isVerifiedArtist(1L)).thenReturn(true);

    assertThatCode(() -> permissionChecker.requireVerifiedArtist(1L)).doesNotThrowAnyException();
  }

  @Test
  void unverifiedArtistIsDenied() {
    when(artistVerificationRepository.isVerifiedArtist(1L)).thenReturn(false);

    assertError(
        () -> permissionChecker.requireVerifiedArtist(1L),
        PersonalArtworkErrorCode.NOT_VERIFIED_ARTIST);
  }

  @Test
  void nonOwnerIsDenied() {
    PersonalArtwork personalArtwork = mock(PersonalArtwork.class);
    when(personalArtwork.isOwnedBy(2L)).thenReturn(false);

    assertError(
        () -> permissionChecker.requireOwner(personalArtwork, 2L), GlobalErrorCode.FORBIDDEN);
  }

  private void assertError(Runnable action, Object errorCode) {
    assertThatThrownBy(action::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                org.assertj.core.api.Assertions.assertThat(exception.errorCode())
                    .isEqualTo(errorCode));
  }
}
