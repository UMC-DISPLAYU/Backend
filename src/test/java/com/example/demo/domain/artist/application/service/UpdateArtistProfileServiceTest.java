package com.example.demo.domain.artist.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artist.application.command.UpdateArtistProfileCommand;
import com.example.demo.domain.artist.application.permission.ArtistPermissionChecker;
import com.example.demo.domain.artist.application.result.UpdateArtistProfileResult;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;
import com.example.demo.domain.artist.domain.model.ArtistUserQualification;
import com.example.demo.domain.artist.domain.repository.AreaOfActivityRepository;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import com.example.demo.domain.artist.domain.repository.ArtistUserQualificationRepository;
import com.example.demo.domain.artist.domain.type.ActivityCategory;
import com.example.demo.domain.user.contract.command.ChangeUserUniversityCommand;
import com.example.demo.domain.user.contract.command.ChangeUserUniversityResult;
import com.example.demo.domain.user.contract.command.ChangeUserUniversityUseCase;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UpdateArtistProfileServiceTest {

  private static final Long USER_ID = 1L;
  private static final Instant UPDATED_AT = Instant.parse("2026-09-27T00:00:00Z");
  private final ArtistUserQualificationRepository qualificationRepository =
      mock(ArtistUserQualificationRepository.class);
  private final ArtistProfileRepository artistProfileRepository =
      mock(ArtistProfileRepository.class);
  private final AreaOfActivityRepository areaOfActivityRepository =
      mock(AreaOfActivityRepository.class);
  private final ChangeUserUniversityUseCase changeUserUniversityUseCase =
      mock(ChangeUserUniversityUseCase.class);
  private final UpdateArtistProfileService service =
      new UpdateArtistProfileService(
          qualificationRepository,
          artistProfileRepository,
          areaOfActivityRepository,
          changeUserUniversityUseCase,
          new ArtistPermissionChecker());

  @Test
  void updatesArtistProfileInOneFlow() {
    ArtistProfile profile =
        ArtistProfile.create(USER_ID, "artist", "artist@du.ac.kr", "기존대학교", null);
    prepare(profile);
    when(artistProfileRepository.existsByArtistName("newName")).thenReturn(false);
    when(changeUserUniversityUseCase.execute(new ChangeUserUniversityCommand(USER_ID, "서양대학교")))
        .thenReturn(new ChangeUserUniversityResult("서양대학교"));

    UpdateArtistProfileResult result = service.execute(command("newName"));

    assertThat(profile.getArtistName()).isEqualTo("newName");
    assertThat(profile.getProfileImageUrl())
        .isEqualTo("https://d1tdgnysscm2va.cloudfront.net/images/user/profile.jpg");
    assertThat(profile.getIntroduction()).isEqualTo("작가 소개");
    assertThat(profile.getPortfolioUrl()).isEqualTo("https://portfolio.example.com");
    assertThat(profile.getUnivName()).isEqualTo("서양대학교");
    assertThat(result.artistName()).isEqualTo("newName");
    assertThat(result.fields()).containsExactly(ActivityCategory.DESIGN, ActivityCategory.VIDEO);
    verify(areaOfActivityRepository).deleteAllByArtistProfile(profile);
  }

  @Test
  void skipsUniversityCommandWhenUniversityIsNotChanged() {
    ArtistProfile profile =
        ArtistProfile.create(USER_ID, "sameName", "artist@du.ac.kr", "기존대학교", null);
    prepare(profile);
    UpdateArtistProfileCommand command = command("sameName", null);

    service.execute(command);

    verify(changeUserUniversityUseCase, never())
        .execute(org.mockito.ArgumentMatchers.any(ChangeUserUniversityCommand.class));
    assertThat(profile.getUnivName()).isEqualTo("기존대학교");
  }

  @Test
  void rejectsDuplicateArtistName() {
    ArtistProfile profile =
        ArtistProfile.create(USER_ID, "oldArtist", "artist@du.ac.kr", "기존대학교", null);
    prepare(profile);
    when(artistProfileRepository.existsByArtistName("newName")).thenReturn(true);
    when(changeUserUniversityUseCase.execute(new ChangeUserUniversityCommand(USER_ID, "서양대학교")))
        .thenReturn(new ChangeUserUniversityResult("서양대학교"));

    assertThatExceptionOfType(ArtistException.class)
        .isThrownBy(() -> service.execute(command("newName")))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(ArtistErrorCode.DUPLICATE_ARTIST_NAME));
  }

  @Test
  void rejectsWithdrawnUserFromLocalQualification() {
    when(qualificationRepository.findByUserId(USER_ID))
        .thenReturn(Optional.of(qualification(true)));

    assertThatExceptionOfType(ArtistException.class)
        .isThrownBy(() -> service.execute(command("newName")))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArtistErrorCode.ARTIST_VERIFICATION_REQUIRED));
  }

  @Test
  void rejectsDuplicateActivityFields() {
    ArtistProfile profile =
        ArtistProfile.create(USER_ID, "artist", "artist@du.ac.kr", "기존대학교", null);
    prepare(profile);
    UpdateArtistProfileCommand command =
        new UpdateArtistProfileCommand(
            USER_ID,
            null,
            "artist",
            "작가 소개",
            List.of(ActivityCategory.DESIGN, ActivityCategory.DESIGN),
            null,
            null);

    assertThatExceptionOfType(ArtistException.class)
        .isThrownBy(() -> service.execute(command))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArtistErrorCode.INVALID_ACTIVITY_FIELDS));
  }

  private void prepare(ArtistProfile profile) {
    when(qualificationRepository.findByUserId(USER_ID))
        .thenReturn(Optional.of(qualification(false)));
    when(artistProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.of(profile));
  }

  private ArtistUserQualification qualification(boolean withdrawn) {
    return ArtistUserQualification.restore(
        USER_ID, "artist@du.ac.kr", "기존대학교", true, withdrawn, 1L, null, UPDATED_AT);
  }

  private UpdateArtistProfileCommand command(String artistName) {
    return command(artistName, "서양대학교");
  }

  private UpdateArtistProfileCommand command(String artistName, String universityName) {
    return new UpdateArtistProfileCommand(
        USER_ID,
        "https://d1tdgnysscm2va.cloudfront.net/images/user/profile.jpg",
        artistName,
        "작가 소개",
        List.of(ActivityCategory.DESIGN, ActivityCategory.VIDEO),
        "https://portfolio.example.com",
        universityName);
  }
}
