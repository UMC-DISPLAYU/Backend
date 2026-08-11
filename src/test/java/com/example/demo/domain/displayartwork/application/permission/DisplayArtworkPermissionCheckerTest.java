package com.example.demo.domain.displayartwork.application.permission;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.ArtistVerificationRepository;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import com.example.demo.domain.displayartwork.domain.type.CreatorRole;
import com.example.demo.global.error.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DisplayArtworkPermissionCheckerTest {

  private static final Long ARTWORK_ID = 10L;
  private static final Long OWNER = 1L;
  private static final Long ARTIST = 2L;
  private static final Long MEMBER = 3L;
  private static final Long TEAM_LEADER = 4L;

  private final CreatorRepository creatorRepository = mock(CreatorRepository.class);
  private final ArtistVerificationRepository artistVerificationRepository =
      mock(ArtistVerificationRepository.class);
  private final DisplayArtworkPermissionChecker checker =
      new DisplayArtworkPermissionChecker(creatorRepository, artistVerificationRepository);

  @Test
  void ownerCanRegisterArtworkWithoutBeingTeamMember() {
    when(artistVerificationRepository.isVerifiedArtist(OWNER)).thenReturn(true);

    assertThatCode(() -> checker.requireArtworkRegistrant(OWNER, display()))
        .doesNotThrowAnyException();
  }

  @Test
  void unrelatedTeamMemberCannotEditArtwork() {
    when(creatorRepository.findByDisplayArtworkId(ARTWORK_ID))
        .thenReturn(
            List.of(
                new Creator(null, "artist", false, CreatorRole.LEAD_ARTIST, ARTIST, ARTWORK_ID)));

    assertThatThrownBy(() -> checker.requireArtworkEditor(MEMBER, display(), ARTWORK_ID))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_EDIT);
  }

  @Test
  void unrelatedTeamMemberCannotDeleteArtwork() {
    when(creatorRepository.findByDisplayArtworkId(ARTWORK_ID))
        .thenReturn(
            List.of(
                new Creator(null, "artist", false, CreatorRole.LEAD_ARTIST, ARTIST, ARTWORK_ID)));

    assertThatThrownBy(() -> checker.requireArtworkDeleter(MEMBER, display(), ARTWORK_ID))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_ACTION);
  }

  @Test
  void creatorCanEditArtwork() {
    when(creatorRepository.findByDisplayArtworkId(ARTWORK_ID))
        .thenReturn(
            List.of(
                new Creator(null, "artist", false, CreatorRole.LEAD_ARTIST, ARTIST, ARTWORK_ID)));

    assertThatCode(() -> checker.requireArtworkEditor(ARTIST, display(), ARTWORK_ID))
        .doesNotThrowAnyException();
  }

  @Test
  void nonOwnerTeamLeaderCanEditArtwork() {
    assertThatCode(() -> checker.requireArtworkEditor(TEAM_LEADER, display(), ARTWORK_ID))
        .doesNotThrowAnyException();
  }

  @Test
  void onlyDisplayLeaderCanEditArtworkRegisteredWithCreatorNameOnly() {
    when(creatorRepository.findByDisplayArtworkId(ARTWORK_ID))
        .thenReturn(
            List.of(
                new Creator(
                    null, "accountless artist", false, CreatorRole.LEAD_ARTIST, null, ARTWORK_ID)));

    assertThatCode(() -> checker.requireArtworkEditor(OWNER, display(), ARTWORK_ID))
        .doesNotThrowAnyException();
    assertThatThrownBy(() -> checker.requireArtworkEditor(ARTIST, display(), ARTWORK_ID))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_EDIT);
    assertThatThrownBy(() -> checker.requireArtworkEditor(MEMBER, display(), ARTWORK_ID))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_EDIT);
  }

  @Test
  void unauthenticatedUserCannotEditArtwork() {
    assertThatThrownBy(() -> checker.requireArtworkEditor(null, display(), ARTWORK_ID))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_EDIT);
  }

  @Test
  void onlyOwnerCanEditArtworkOrder() {
    assertThatCode(() -> checker.requireArtworkOrderEditor(OWNER, display()))
        .doesNotThrowAnyException();
    assertThatThrownBy(() -> checker.requireArtworkOrderEditor(TEAM_LEADER, display()))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_ORDER_EDIT);
  }

  @Test
  void displayLeaderCanRegisterArtworkForAnotherArtist() {
    assertThatCode(() -> checker.requireProxyRegistrationAllowed(TEAM_LEADER, display(), ARTIST))
        .doesNotThrowAnyException();
  }

  @Test
  void teamMemberCannotRegisterArtworkForAnotherArtist() {
    assertThatThrownBy(() -> checker.requireProxyRegistrationAllowed(MEMBER, display(), ARTIST))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.FORBIDDEN_PROXY_ARTWORK_REGISTRATION);
  }

  @Test
  void qnaHandlerMustBeArtistOrDisplayLeader() {
    assertThatCode(() -> checker.requireQnaHandlerAssignable(display(), ARTIST, Set.of(ARTIST)))
        .doesNotThrowAnyException();
    assertThatThrownBy(() -> checker.requireQnaHandlerAssignable(display(), MEMBER, Set.of(ARTIST)))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.INVALID_QA_HANDLER);
  }

  @Test
  void unverifiedArtistParticipantIsRejectedWithRequestedErrorCode() {
    when(artistVerificationRepository.isVerifiedArtist(ARTIST)).thenReturn(false);

    assertThatThrownBy(
            () ->
                checker.requireVerifiedArtistParticipant(
                    display(), ARTIST, DisplayArtworkErrorCode.NOT_VERIFIED_ARTIST))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.NOT_VERIFIED_ARTIST);
  }

  @Test
  void deletedVerifiedArtistTeamMemberIsRejectedWithRequestedErrorCode() {
    Display display = display();
    display.getTeamMembers().stream()
        .filter(member -> member.getUserId().value().equals(ARTIST))
        .findFirst()
        .orElseThrow()
        .softDelete(LocalDateTime.of(2026, 5, 1, 0, 0));
    when(artistVerificationRepository.isVerifiedArtist(ARTIST)).thenReturn(true);

    assertThatThrownBy(
            () ->
                checker.requireVerifiedArtistTeamMember(
                    display, ARTIST, DisplayArtworkErrorCode.INVALID_ARTIST_USER_ID))
        .isInstanceOf(BusinessException.class)
        .extracting(exception -> ((BusinessException) exception).errorCode())
        .isEqualTo(DisplayArtworkErrorCode.INVALID_ARTIST_USER_ID);
  }

  private static Display display() {
    Display display =
        Display.create(
            new UserId(OWNER),
            "display",
            "poster",
            "subtitle",
            "content",
            new DisplayLocation("place", BigDecimal.ONE, BigDecimal.ONE),
            "",
            "",
            "organization",
            "department",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 2),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.IMMEDIATELY);
    display.addTeamMember(
        new TeamMember(1L, new UserId(ARTIST), "artist", TeamMemberRole.TEAM_MEM, true));
    display.addTeamMember(
        new TeamMember(2L, new UserId(MEMBER), "member", TeamMemberRole.TEAM_MEM, true));
    display.addTeamMember(
        new TeamMember(3L, new UserId(TEAM_LEADER), "leader", TeamMemberRole.TEAM_LEADER, true));
    return display;
  }
}
