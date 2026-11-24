package com.example.demo.domain.display.application.notification;

import com.example.demo.domain.archive.application.usecase.GetDisplaySaversUseCase;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import com.example.demo.domain.notification.application.usecase.RequestNotificationUseCase;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 오늘 시작하는 공개 전시를 시작일 전에 저장한 사용자에게 알린다. 같은 전시로 이미 받은 사람은 알림 모듈이 빼고 저장하므로 하루에 여러 번 돌아도 한 번만 간다. */
@Service
public class NotifyStartedDisplaysService {

  private final DisplayRepository displayRepository;
  private final GetDisplaySaversUseCase getDisplaySaversUseCase;
  private final RequestNotificationUseCase requestNotificationUseCase;
  private final Clock clock;

  public NotifyStartedDisplaysService(
      DisplayRepository displayRepository,
      GetDisplaySaversUseCase getDisplaySaversUseCase,
      RequestNotificationUseCase requestNotificationUseCase,
      Clock clock) {
    this.displayRepository = displayRepository;
    this.getDisplaySaversUseCase = getDisplaySaversUseCase;
    this.requestNotificationUseCase = requestNotificationUseCase;
    this.clock = clock;
  }

  @Transactional
  public void notifyDisplaysStartingToday() {
    LocalDate today = LocalDate.now(clock);
    // 저장 시각은 UTC로 저장되므로 "시작일 0시(KST) 전"을 UTC로 바꿔 비교한다.
    LocalDateTime savedBefore =
        LocalDateTime.ofInstant(today.atStartOfDay(clock.getZone()).toInstant(), ZoneOffset.UTC);

    for (Display display : displayRepository.findPublishedStartingOn(today)) {
      notifySavers(display, savedBefore);
    }
  }

  private void notifySavers(Display display, LocalDateTime savedBefore) {
    // 참여한 팀원은 이미 알고 있으므로 받지 않는다.
    Set<Long> memberUserIds =
        display.getTeamMembers().stream()
            .filter(TeamMember::isAccepted)
            .filter(member -> !member.isDeleted())
            .map(member -> member.getUserId().value())
            .collect(Collectors.toSet());
    List<Long> recipients =
        getDisplaySaversUseCase.getSaverUserIds(display.getId(), savedBefore).stream()
            .filter(userId -> !memberUserIds.contains(userId))
            .toList();
    if (recipients.isEmpty()) {
      return;
    }
    requestNotificationUseCase.request(
        recipients,
        null,
        NotificationMessages.exhibitionStarted(
            display.getTitle(),
            display.getPeriod().startDate(),
            display.getPeriod().endDate(),
            display.getLocation() == null ? null : display.getLocation().placeName(),
            display.getId()));
  }
}
