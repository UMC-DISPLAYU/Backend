package com.example.demo.domain.display.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.application.command.UpdateMyDisplayNicknameCommand;
import com.example.demo.domain.display.application.port.ArtworkCreatorRenamePort;
import com.example.demo.domain.display.application.port.DisplayListCacheEvictionPort;
import com.example.demo.domain.display.application.result.DisplayMemberResult;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.TeamMemberRepository;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class UpdateMyDisplayNicknameServiceTest {

  private static final Long DISPLAY_ID = 10L;
  private static final Long USER_ID = 20L;

  private final TeamMemberRepository teamMemberRepository = mock(TeamMemberRepository.class);
  private final UserRepository userRepository = mock(UserRepository.class);
  private final ArtworkCreatorRenamePort artworkCreatorRenamePort =
      mock(ArtworkCreatorRenamePort.class);
  private final DisplayListCacheEvictionPort displayListCacheEvictionPort =
      mock(DisplayListCacheEvictionPort.class);
  private final UpdateMyDisplayNicknameService service =
      new UpdateMyDisplayNicknameService(
          teamMemberRepository,
          userRepository,
          artworkCreatorRenamePort,
          displayListCacheEvictionPort);

  @Test
  void updateNicknameRenamesArtworkCreatorsWithPreviousDisplayNickname() {
    TeamMember teamMember =
        new TeamMember(null, new UserId(USER_ID), "beanie", TeamMemberRole.TEAM_MEM, true);
    when(teamMemberRepository.findAcceptedByDisplayIdAndUserId(DISPLAY_ID, USER_ID))
        .thenReturn(Optional.of(teamMember));
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

    DisplayMemberResult result =
        service.updateNickname(new UpdateMyDisplayNicknameCommand(USER_ID, DISPLAY_ID, "세현"));

    assertThat(result.displayNickname()).isEqualTo("세현");
    verify(artworkCreatorRenamePort).rename(DISPLAY_ID, USER_ID, "beanie", "세현");
  }

  @Test
  void updateNicknameEvictsDisplayListCacheAfterRenameRequest() {
    TeamMember teamMember =
        new TeamMember(null, new UserId(USER_ID), "beanie", TeamMemberRole.TEAM_MEM, true);
    when(teamMemberRepository.findAcceptedByDisplayIdAndUserId(DISPLAY_ID, USER_ID))
        .thenReturn(Optional.of(teamMember));
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

    service.updateNickname(new UpdateMyDisplayNicknameCommand(USER_ID, DISPLAY_ID, "세현"));

    InOrder inOrder = inOrder(artworkCreatorRenamePort, displayListCacheEvictionPort);
    inOrder.verify(artworkCreatorRenamePort).rename(DISPLAY_ID, USER_ID, "beanie", "세현");
    inOrder.verify(displayListCacheEvictionPort).evictAfterCommit();
  }

  @Test
  void updateNicknameDoesNotRenameOrEvictCacheWhenTeamMemberIsMissing() {
    when(teamMemberRepository.findAcceptedByDisplayIdAndUserId(DISPLAY_ID, USER_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                service.updateNickname(
                    new UpdateMyDisplayNicknameCommand(USER_ID, DISPLAY_ID, "세현")))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.DISPLAY_MEMBER_NOT_FOUND));

    verify(artworkCreatorRenamePort, never())
        .rename(anyLong(), anyLong(), anyString(), anyString());
    verify(displayListCacheEvictionPort, never()).evictAfterCommit();
  }
}
