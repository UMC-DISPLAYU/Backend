package com.example.demo.domain.display.application.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.result.ArtistSaverResult;
import com.example.demo.domain.archive.application.usecase.GetArtistSaversUseCase;
import com.example.demo.domain.artist.application.result.ArtistProfileSummaryResult;
import com.example.demo.domain.artist.application.usecase.GetArtistProfileSummariesUseCase;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.application.usecase.CountDisplayArtworksByArtistUseCase;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import com.example.demo.domain.notification.application.usecase.RequestNotificationUseCase;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DisplayNotifierTest {

  private static final Long DISPLAY_ID = 5L;
  private static final String TITLE = "재생 중";
  private static final Long REVIEWER = 900L;
  private static final Long LEADER = 1L;
  private static final Long MEMBER = 2L;
  private static final Long OTHER_MEMBER = 3L;
  private static final Long FAN = 50L;
  private static final Long OTHER_FAN = 51L;
  private static final LocalDate START = LocalDate.of(2026, 12, 2);
  private static final LocalDate END = LocalDate.of(2026, 12, 7);
  private static final String PLACE = "미온미디어관 B1";

  private final RequestNotificationUseCase requestNotificationUseCase =
      mock(RequestNotificationUseCase.class);
  private final UserRepository userRepository = mock(UserRepository.class);
  private final CountDisplayArtworksByArtistUseCase countUseCase =
      mock(CountDisplayArtworksByArtistUseCase.class);
  private final GetArtistSaversUseCase saversUseCase = mock(GetArtistSaversUseCase.class);
  private final GetArtistProfileSummariesUseCase profilesUseCase =
      mock(GetArtistProfileSummariesUseCase.class);
  private final DisplayNotifier notifier =
      new DisplayNotifier(
          requestNotificationUseCase, userRepository, countUseCase, saversUseCase, profilesUseCase);

  private final Display display = mock(Display.class);

  @BeforeEach
  void setUp() {
    when(display.getId()).thenReturn(DISPLAY_ID);
    when(display.getTitle()).thenReturn(TITLE);
    when(display.getPeriod())
        .thenReturn(new DisplayPeriod(START, END, LocalTime.of(10, 0), LocalTime.of(18, 0)));
    when(display.getLocation())
        .thenReturn(new DisplayLocation(PLACE, BigDecimal.ONE, BigDecimal.ONE));
    when(display.getTeamMembers())
        .thenReturn(
            List.of(
                new TeamMember(11L, new UserId(LEADER), "김도윤", TeamMemberRole.TEAM_LEADER, true),
                new TeamMember(12L, new UserId(MEMBER), "송하윤", TeamMemberRole.TEAM_MEM, true),
                new TeamMember(13L, new UserId(OTHER_MEMBER), "서유진", TeamMemberRole.MANAGER, true),
                new TeamMember(14L, new UserId(99L), "초대 대기", TeamMemberRole.TEAM_MEM, false)));
    when(profilesUseCase.getArtistProfileSummaries(any()))
        .thenReturn(
            List.of(
                new ArtistProfileSummaryResult(102L, MEMBER, "송하윤", null, List.of()),
                new ArtistProfileSummaryResult(103L, OTHER_MEMBER, "서유진", null, List.of())));
  }

  @Test
  void reviewApprovedNotifiesLeaderOnly() {
    when(saversUseCase.getArtistSavers(anyCollection())).thenReturn(List.of());

    notifier.notifyReviewApproved(display, REVIEWER);

    verify(requestNotificationUseCase)
        .request(List.of(LEADER), REVIEWER, NotificationMessages.requestApproved(TITLE));
  }

  @Test
  void reviewApprovedNotifiesSaversWithFirstSavedArtistAndOthersCount() {
    LocalDateTime earlier = LocalDateTime.of(2026, 11, 1, 0, 0);
    LocalDateTime later = LocalDateTime.of(2026, 11, 2, 0, 0);
    when(saversUseCase.getArtistSavers(anyCollection()))
        .thenReturn(
            List.of(
                // FAN은 서유진을 나중에, 송하윤을 먼저 저장했다 → "송하윤님 외 1명"
                new ArtistSaverResult(FAN, OTHER_MEMBER, 103L, later),
                new ArtistSaverResult(FAN, MEMBER, 102L, earlier),
                new ArtistSaverResult(OTHER_FAN, MEMBER, 102L, earlier),
                // 같은 전시의 팀원이 동료를 저장한 경우는 받지 않는다.
                new ArtistSaverResult(OTHER_MEMBER, MEMBER, 102L, earlier)));

    notifier.notifyReviewApproved(display, REVIEWER);

    verify(requestNotificationUseCase)
        .request(
            List.of(FAN),
            null,
            NotificationMessages.artistNewExhibition(
                "송하윤", 1, TITLE, START, END, PLACE, DISPLAY_ID));
    verify(requestNotificationUseCase)
        .request(
            List.of(OTHER_FAN),
            null,
            NotificationMessages.artistNewExhibition(
                "송하윤", 0, TITLE, START, END, PLACE, DISPLAY_ID));
    verify(requestNotificationUseCase, never())
        .request(argThat(recipients -> recipients.contains(OTHER_MEMBER)), any(), any());
  }

  @Test
  void reviewRejectedNotifiesLeaderWithReason() {
    notifier.notifyReviewRejected(display, REVIEWER, "이미지 출처를 확인할 수 없어요.");

    verify(requestNotificationUseCase)
        .request(
            List.of(LEADER),
            REVIEWER,
            NotificationMessages.requestRejected(TITLE, "이미지 출처를 확인할 수 없어요."));
  }

  @Test
  void invitedShowsInviterDisplayNicknameAndServiceNickname() {
    User inviter = mock(User.class);
    when(inviter.getNickname()).thenReturn("doyoon");
    when(userRepository.findById(LEADER)).thenReturn(Optional.of(inviter));

    notifier.notifyInvited(display, LEADER, 77L);

    verify(requestNotificationUseCase)
        .request(
            List.of(77L), LEADER, NotificationMessages.exhibitionInvited(TITLE, "김도윤", "doyoon"));
  }

  @Test
  void memberRemovedNotifiesRemovedMemberWithDeletedArtworkCount() {
    notifier.notifyMemberRemoved(display, MEMBER, LEADER, 2);

    verify(requestNotificationUseCase)
        .request(List.of(MEMBER), LEADER, NotificationMessages.exhibitionMemberRemoved(TITLE, 2));
  }

  @Test
  void displayDeletedGroupsMembersByTheirOwnArtworkCount() {
    when(countUseCase.countByArtist(DISPLAY_ID)).thenReturn(Map.of(MEMBER, 5, LEADER, 1));

    notifier.notifyDisplayDeleted(display, LEADER);

    // 삭제한 대표자는 요청 창구에서 actor로 제외되므로 같은 묶음에 들어 있어도 받지 않는다.
    verify(requestNotificationUseCase)
        .request(List.of(MEMBER), LEADER, NotificationMessages.exhibitionDeleted(TITLE, 5));
    verify(requestNotificationUseCase)
        .request(List.of(OTHER_MEMBER), LEADER, NotificationMessages.exhibitionDeleted(TITLE, 0));
    verify(requestNotificationUseCase)
        .request(
            argThat(recipients -> new HashSet<>(recipients).equals(Set.of(LEADER))),
            eq(LEADER),
            eq(NotificationMessages.exhibitionDeleted(TITLE, 1)));
  }
}
