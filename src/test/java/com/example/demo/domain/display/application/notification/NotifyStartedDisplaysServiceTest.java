package com.example.demo.domain.display.application.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.usecase.GetDisplaySaversUseCase;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import com.example.demo.domain.notification.application.usecase.RequestNotificationUseCase;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotifyStartedDisplaysServiceTest {

  private static final LocalDate TODAY = LocalDate.of(2026, 12, 4);
  // 시작일 0시(KST)는 UTC로 전날 15시다.
  private static final LocalDateTime SAVED_BEFORE_UTC = LocalDateTime.of(2026, 12, 3, 15, 0);
  private static final Long DISPLAY_ID = 5L;
  private static final Long LEADER = 1L;

  private final DisplayRepository displayRepository = mock(DisplayRepository.class);
  private final GetDisplaySaversUseCase saversUseCase = mock(GetDisplaySaversUseCase.class);
  private final RequestNotificationUseCase requestNotificationUseCase =
      mock(RequestNotificationUseCase.class);
  private final NotifyStartedDisplaysService service =
      new NotifyStartedDisplaysService(
          displayRepository,
          saversUseCase,
          requestNotificationUseCase,
          Clock.fixed(Instant.parse("2026-12-04T00:00:00Z"), ZoneId.of("Asia/Seoul")));

  @Test
  void notifiesSaversExceptTeamMembersWithStartedMessage() {
    Display display = display();
    when(displayRepository.findPublishedStartingOn(TODAY)).thenReturn(List.of(display));
    when(saversUseCase.getSaverUserIds(DISPLAY_ID, SAVED_BEFORE_UTC))
        .thenReturn(List.of(50L, LEADER, 51L));

    service.notifyDisplaysStartingToday();

    verify(requestNotificationUseCase)
        .request(
            List.of(50L, 51L),
            null,
            NotificationMessages.exhibitionStarted(
                "빛이 지나간 자리", TODAY, LocalDate.of(2026, 12, 6), "스튜디오 여백 B1", DISPLAY_ID));
  }

  @Test
  void doesNotRequestWhenOnlyTeamMembersSavedTheDisplay() {
    Display display = display();
    when(displayRepository.findPublishedStartingOn(TODAY)).thenReturn(List.of(display));
    when(saversUseCase.getSaverUserIds(DISPLAY_ID, SAVED_BEFORE_UTC)).thenReturn(List.of(LEADER));

    service.notifyDisplaysStartingToday();

    verify(requestNotificationUseCase, never()).request(any(), any(), any());
  }

  private static Display display() {
    Display display = mock(Display.class);
    when(display.getId()).thenReturn(DISPLAY_ID);
    when(display.getTitle()).thenReturn("빛이 지나간 자리");
    when(display.getPeriod())
        .thenReturn(
            new DisplayPeriod(
                TODAY, LocalDate.of(2026, 12, 6), LocalTime.of(10, 0), LocalTime.of(18, 0)));
    when(display.getLocation())
        .thenReturn(new DisplayLocation("스튜디오 여백 B1", BigDecimal.ONE, BigDecimal.ONE));
    when(display.getTeamMembers())
        .thenReturn(
            List.of(
                new TeamMember(11L, new UserId(LEADER), "대표", TeamMemberRole.TEAM_LEADER, true)));
    return display;
  }
}
