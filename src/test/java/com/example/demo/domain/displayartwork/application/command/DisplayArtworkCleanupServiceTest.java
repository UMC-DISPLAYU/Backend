package com.example.demo.domain.displayartwork.application.command;

import static com.example.demo.domain.displayartwork.application.command.DisplayArtworkNotificationFixtures.DISPLAY_TITLE;
import static com.example.demo.domain.displayartwork.application.command.DisplayArtworkNotificationFixtures.LEADER;
import static com.example.demo.domain.displayartwork.application.command.DisplayArtworkNotificationFixtures.MEMBER;
import static com.example.demo.domain.displayartwork.application.command.DisplayArtworkNotificationFixtures.OTHER_MEMBER;
import static com.example.demo.domain.displayartwork.application.command.DisplayArtworkNotificationFixtures.artwork;
import static com.example.demo.domain.displayartwork.application.command.DisplayArtworkNotificationFixtures.display;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.domain.displayartwork.domain.type.CreatorRole;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import com.example.demo.domain.notification.application.usecase.RequestNotificationUseCase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class DisplayArtworkCleanupServiceTest {

  private static final Long DISPLAY_ID = 5L;

  private final DisplayArtworkRepository displayArtworkRepository =
      mock(DisplayArtworkRepository.class);
  private final CreatorRepository creatorRepository = mock(CreatorRepository.class);
  private final RequestNotificationUseCase requestNotificationUseCase =
      mock(RequestNotificationUseCase.class);
  private final DisplayArtworkCleanupService service =
      new DisplayArtworkCleanupService(
          displayArtworkRepository,
          creatorRepository,
          mock(ApplicationEventPublisher.class),
          requestNotificationUseCase);

  @Test
  void deleteByArtistAccountNotifiesRemainingArtistsAndRegistrantsOncePerRecipient() {
    Display display = display();
    // 나가는 작가는 MEMBER. A·C는 다른 팀원과 공동 작품이고 대표자가 등록, B는 MEMBER 혼자 등록
    DisplayArtwork a = artwork(display, 100L, "얇은 경계", LEADER);
    DisplayArtwork b = artwork(display, 101L, "혼자 만든 작품", MEMBER);
    DisplayArtwork c = artwork(display, 102L, "비가 지난 가방", LEADER);
    when(displayArtworkRepository.findAllActiveByDisplayIdAndArtistUserId(DISPLAY_ID, MEMBER))
        .thenReturn(List.of(a, b, c));
    when(creatorRepository.findArtistsByDisplayArtworkIds(List.of(100L, 101L, 102L)))
        .thenReturn(
            List.of(
                creator(CreatorRole.LEAD_ARTIST, MEMBER, 100L),
                creator(CreatorRole.CO_AUTHOR, OTHER_MEMBER, 100L),
                creator(CreatorRole.CO_AUTHOR, null, 100L),
                creator(CreatorRole.LEAD_ARTIST, MEMBER, 101L),
                creator(CreatorRole.LEAD_ARTIST, MEMBER, 102L),
                creator(CreatorRole.CO_AUTHOR, OTHER_MEMBER, 102L)));

    int deletedCount = service.deleteByArtistAccount(DISPLAY_ID, MEMBER, LEADER);

    assertThat(deletedCount).isEqualTo(3);
    assertThat(List.of(a, b, c)).allMatch(DisplayArtwork::isDeleted);
    var expected =
        NotificationMessages.artworksDeletedByArtistLeave(
            List.of("얇은 경계", "비가 지난 가방"), DISPLAY_TITLE);
    verify(requestNotificationUseCase).request(List.of(OTHER_MEMBER), LEADER, expected);
    verify(requestNotificationUseCase).request(List.of(LEADER), LEADER, expected);
    verify(requestNotificationUseCase, times(2)).request(any(), any(), any());
  }

  @Test
  void deleteByArtistAccountDoesNothingWhenArtistHasNoArtworks() {
    when(displayArtworkRepository.findAllActiveByDisplayIdAndArtistUserId(DISPLAY_ID, MEMBER))
        .thenReturn(List.of());

    assertThat(service.deleteByArtistAccount(DISPLAY_ID, MEMBER, MEMBER)).isZero();
    verifyNoInteractions(requestNotificationUseCase, creatorRepository);
  }

  private static Creator creator(CreatorRole role, Long userId, Long artworkId) {
    return new Creator(null, "작가", false, role, userId, artworkId);
  }
}
