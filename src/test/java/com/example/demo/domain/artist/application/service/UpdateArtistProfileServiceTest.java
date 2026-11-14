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
import com.example.demo.domain.artist.domain.repository.AreaOfActivityRepository;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import com.example.demo.domain.artist.domain.type.ActivityCategory;
import com.example.demo.domain.user.domain.aggregate.University;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.service.SchoolEmailValidator;
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
  private final SchoolEmailValidator schoolEmailValidator = mock(SchoolEmailValidator.class);
  private final UpdateArtistProfileService service =
      new UpdateArtistProfileService(
          userRepository,
          artistProfileRepository,
          areaOfActivityRepository,
          schoolEmailValidator,
          new ArtistPermissionChecker());

  @Test
  void updatesArtistProfileInOneFlow() {
    User user =
        User.builder()
            .id(USER_ID)
            .nickname("oldName")
            .profileImageUrl("https://cdn.example.com/my-profile.jpg")
            .isVerified(true)
            .build();
    ArtistProfile profile = ArtistProfile.create(user, "artist", "artist@du.ac.kr", "기존대학교", null);
    prepare(user, profile);
    when(artistProfileRepository.existsByArtistName("newName")).thenReturn(false);

    UpdateArtistProfileResult result = service.execute(command("newName"));

    assertThat(user.getNickname()).isEqualTo("oldName");
    assertThat(profile.getArtistName()).isEqualTo("newName");
    assertThat(profile.getProfileImageUrl())
        .isEqualTo("https://d1tdgnysscm2va.cloudfront.net/images/user/profile.jpg");
    assertThat(user.getProfileImageUrl()).isEqualTo("https://cdn.example.com/my-profile.jpg");
    assertThat(user.getUnivName()).isEqualTo("한양대학교");
    assertThat(profile.getIntroduction()).isEqualTo("작가 소개");
    assertThat(profile.getPortfolioUrl()).isEqualTo("https://portfolio.example.com");
    assertThat(profile.getInstagramUrl()).isEqualTo("https://www.instagram.com/example.artist");
    assertThat(profile.getUnivName()).isEqualTo("한양대학교");
    assertThat(result.profileImageUrl())
        .isEqualTo("https://d1tdgnysscm2va.cloudfront.net/images/user/profile.jpg");
    assertThat(result.artistName()).isEqualTo("newName");
    assertThat(result.fields()).containsExactly(ActivityCategory.DESIGN, ActivityCategory.VIDEO);
    assertThat(result.externalLink()).isEqualTo("https://portfolio.example.com");
    assertThat(result.instagramLink()).isEqualTo("https://www.instagram.com/example.artist");
    verify(areaOfActivityRepository).deleteAllByArtistProfile(profile);
  }

  @Test
  void updatesOnlyInstagramLink() {
    User user = verifiedUser("artist");
    ArtistProfile profile =
        ArtistProfile.create(
            user, "artist", "artist@du.ac.kr", "기존대학교", "https://portfolio.example.com");
    profile.updateProfile(
        null, "https://portfolio.example.com", "https://www.instagram.com/old.artist", "기존대학교");
    prepare(user, profile);

    service.execute(
        command("artist", "https://portfolio.example.com", "https://www.instagram.com/new.artist"));

    assertThat(profile.getPortfolioUrl()).isEqualTo("https://portfolio.example.com");
    assertThat(profile.getInstagramUrl()).isEqualTo("https://www.instagram.com/new.artist");
  }

  @Test
  void updatesOnlyPortfolioLink() {
    User user = verifiedUser("artist");
    ArtistProfile profile =
        ArtistProfile.create(
            user, "artist", "artist@du.ac.kr", "기존대학교", "https://old-portfolio.example.com");
    profile.updateProfile(
        null,
        "https://old-portfolio.example.com",
        "https://www.instagram.com/example.artist",
        "기존대학교");
    prepare(user, profile);

    service.execute(
        command(
            "artist",
            "https://new-portfolio.example.com",
            "https://www.instagram.com/example.artist"));

    assertThat(profile.getPortfolioUrl()).isEqualTo("https://new-portfolio.example.com");
    assertThat(profile.getInstagramUrl()).isEqualTo("https://www.instagram.com/example.artist");
  }

  @Test
  void updatesInstagramAndPortfolioLinksTogether() {
    User user = verifiedUser("artist");
    ArtistProfile profile = ArtistProfile.create(user, "artist", "artist@du.ac.kr", "기존대학교", null);
    prepare(user, profile);

    UpdateArtistProfileResult result =
        service.execute(
            command(
                "artist",
                "https://portfolio.example.com",
                "https://www.instagram.com/example.artist"));

    assertThat(profile.getPortfolioUrl()).isEqualTo("https://portfolio.example.com");
    assertThat(profile.getInstagramUrl()).isEqualTo("https://www.instagram.com/example.artist");
    assertThat(result.externalLink()).isEqualTo("https://portfolio.example.com");
    assertThat(result.instagramLink()).isEqualTo("https://www.instagram.com/example.artist");
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
    University university = mock(University.class);
    when(university.getName()).thenReturn("한양대학교");
    when(schoolEmailValidator.validate("한양대학교", user.getSchoolEmail())).thenReturn(university);
  }

  private UpdateArtistProfileCommand command(String artistName) {
    return command(
        artistName, "https://portfolio.example.com", "https://www.instagram.com/example.artist");
  }

  private UpdateArtistProfileCommand command(
      String artistName, String externalLink, String instagramLink) {
    return new UpdateArtistProfileCommand(
        USER_ID,
        "https://d1tdgnysscm2va.cloudfront.net/images/user/profile.jpg",
        artistName,
        "작가 소개",
        List.of(ActivityCategory.DESIGN, ActivityCategory.VIDEO),
        externalLink,
        instagramLink,
        "한양대학교");
  }
}
