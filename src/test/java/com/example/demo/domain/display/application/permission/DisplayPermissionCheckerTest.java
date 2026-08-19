package com.example.demo.domain.display.application.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayContent;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.type.DisplayContentStatus;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import org.junit.jupiter.api.Test;

class DisplayPermissionCheckerTest {

  private static final Long USER_ID = 1L;

  private final DisplayPermissionChecker checker = new DisplayPermissionChecker();

  @Test
  void requireTeamLeaderAllowsTeamLeader() {
    Display display = mock(Display.class);
    when(display.isTeamLeader(USER_ID)).thenReturn(true);

    assertThatCode(() -> checker.requireTeamLeader(display, USER_ID)).doesNotThrowAnyException();
  }

  @Test
  void requireTeamLeaderRejectsNonLeaderWithExistingErrorCode() {
    Display display = mock(Display.class);

    assertThatThrownBy(() -> checker.requireTeamLeader(display, USER_ID))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(GlobalErrorCode.FORBIDDEN));
  }

  @Test
  void requireContentEditorRejectsNonMemberWithExistingErrorCode() {
    Display display = mock(Display.class);

    assertThatThrownBy(() -> checker.requireContentEditor(display, USER_ID))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.DISPLAY_CONTENT_PERMISSION_DENIED));
  }

  @Test
  void requireContentOwnerAllowsUploader() {
    DisplayContent content =
        new DisplayContent(
            1L,
            "https://cdn.displayu.com/content.jpg",
            0,
            DisplayContentStatus.PUBLISHED,
            new UserId(USER_ID));

    assertThatCode(() -> checker.requireContentOwner(content, USER_ID)).doesNotThrowAnyException();
  }

  @Test
  void requireContentOwnerRejectsDifferentUser() {
    DisplayContent content =
        new DisplayContent(
            1L,
            "https://cdn.displayu.com/content.jpg",
            0,
            DisplayContentStatus.PUBLISHED,
            new UserId(2L));

    assertThatThrownBy(() -> checker.requireContentOwner(content, USER_ID))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.DISPLAY_CONTENT_PERMISSION_DENIED));
  }

  @Test
  void requireContentOwnerRejectsLegacyContentWithoutUploader() {
    DisplayContent content = new DisplayContent(1L, "https://cdn.displayu.com/content.jpg", 0);

    assertThatThrownBy(() -> checker.requireContentOwner(content, USER_ID))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.DISPLAY_CONTENT_PERMISSION_DENIED));
  }

  @Test
  void requireInvitationManagerUsesExistingInvitationPolicy() {
    Display display = mock(Display.class);
    when(display.canInviteMember(USER_ID)).thenReturn(true);

    assertThatCode(() -> checker.requireInvitationManager(display, USER_ID))
        .doesNotThrowAnyException();
  }

  @Test
  void requireInvitationTokenManagerRejectsNonLeaderWithExistingErrorCode() {
    Display display = mock(Display.class);

    assertThatThrownBy(() -> checker.requireInvitationTokenManager(display, USER_ID))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.DISPLAY_INVITATION_PERMISSION_DENIED));
  }

  @Test
  void requireExitAllowedRejectsTeamLeaderWithExistingErrorCode() {
    TeamMember teamMember = mock(TeamMember.class);
    when(teamMember.getRole()).thenReturn(TeamMemberRole.TEAM_LEADER);

    assertThatThrownBy(() -> checker.requireExitAllowed(teamMember))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(GlobalErrorCode.FORBIDDEN));
  }
}
