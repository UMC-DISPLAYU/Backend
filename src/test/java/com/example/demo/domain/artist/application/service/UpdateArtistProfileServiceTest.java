package com.example.demo.domain.artist.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artist.application.command.UpdateArtistProfileCommand;
import com.example.demo.domain.artist.application.result.UpdateArtistProfileResult;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import com.example.demo.domain.artist.domain.repository.AreaOfActivityRepository;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import com.example.demo.domain.artist.exception.ArtistErrorCode;
import com.example.demo.domain.artist.exception.ArtistException;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UpdateArtistProfileServiceTest {

  private static final Long USER_ID = 1L;
  private final UserRepository userRepository = mock(UserRepository.class);
  private final ArtistProfileRepository artistProfileRepository =
      mock(ArtistProfileRepository.class);
  private final AreaOfActivityRepository areaOfActivityRepository =
      mock(AreaOfActivityRepository.class);
  private final UpdateArtistProfileService service =
      new UpdateArtistProfileService(
          userRepository, artistProfileRepository, areaOfActivityRepository);

  @Test
  void updatesArtistProfileInOneFlow() {
    User user = verifiedUser("oldName");
    ArtistProfile profile = ArtistProfile.create(user, "artist", "artist@du.ac.kr", "기존대학교", null);
    prepare(user, profile);
    when(artistProfileRepository.existsByArtistName("newName")).thenReturn(false);

    UpdateArtistProfileResult result = service.execute(command("newName"));

    assertThat(user.getNickname()).isEqualTo("oldName");
    assertThat(profile.getArtistName()).isEqualTo("newName");
    assertThat(user.getProfileImageUrl()).isEqualTo("https://cdn.example.com/profile.jpg");
    assertThat(user.getUnivName()).isEqualTo("한양대학교");
    assertThat(profile.getIntroduction()).isEqualTo("작가 소개");
    assertThat(profile.getPortfolioUrl()).isEqualTo("https://portfolio.example.com");
    assertThat(profile.getUnivName()).isEqualTo("한양대학교");
    assertThat(result.artistName()).isEqualTo("newName");
    assertThat(result.fields()).containsExactly(ActivityCategory.DESIGN, ActivityCategory.VIDEO);
    verify(areaOfActivityRepository).deleteAllByArtistProfile(profile);
  }

  @Test
  void skipsDuplicateCheckWhenArtistNameIsUnchanged() {
    User user = verifiedUser("sameName");
    ArtistProfile profile =
        ArtistProfile.create(user, "sameName", "artist@du.ac.kr", "기존대학교", null);
    prepare(user, profile);

    service.execute(command("sameName"));

    verify(artistProfileRepository, never()).existsByArtistName("sameName");
    assertThat(user.getNicknameChangeAt()).isNull();
  }

  @Test
  void rejectsDuplicateArtistName() {
    User user = verifiedUser("nickname");
    ArtistProfile profile =
        ArtistProfile.create(user, "oldArtist", "artist@du.ac.kr", "기존대학교", null);
    prepare(user, profile);
    when(artistProfileRepository.existsByArtistName("newName")).thenReturn(true);

    assertThatExceptionOfType(ArtistException.class)
        .isThrownBy(() -> service.execute(command("newName")))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(ArtistErrorCode.DUPLICATE_ARTIST_NAME));
  }

  @Test
  void rejectsUnverifiedUser() {
    User user = User.builder().id(USER_ID).nickname("oldName").isVerified(false).build();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(() -> service.execute(command("newName")))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(UserErrorCode.ARTIST_VERIFICATION_REQUIRED));
  }

  @Test
  void rejectsDuplicateActivityFields() {
    User user = verifiedUser("oldName");
    ArtistProfile profile = ArtistProfile.create(user, "artist", "artist@du.ac.kr", "기존대학교", null);
    prepare(user, profile);
    UpdateArtistProfileCommand command =
        new UpdateArtistProfileCommand(
            USER_ID,
            null,
            "oldName",
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

  private User verifiedUser(String nickname) {
    return User.builder().id(USER_ID).nickname(nickname).isVerified(true).build();
  }

  private void prepare(User user, ArtistProfile profile) {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(artistProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.of(profile));
  }

  private UpdateArtistProfileCommand command(String artistName) {
    return new UpdateArtistProfileCommand(
        USER_ID,
        "https://cdn.example.com/profile.jpg",
        artistName,
        "작가 소개",
        List.of(ActivityCategory.DESIGN, ActivityCategory.VIDEO),
        "https://portfolio.example.com",
        "한양대학교");
  }
}
