package com.example.demo.domain.displayartwork.application.command;

import static com.example.demo.domain.displayartwork.application.command.DisplayArtworkNotificationFixtures.LEADER;
import static com.example.demo.domain.displayartwork.application.command.DisplayArtworkNotificationFixtures.MEMBER;
import static com.example.demo.domain.displayartwork.application.command.DisplayArtworkNotificationFixtures.OTHER_MEMBER;
import static com.example.demo.domain.displayartwork.application.command.DisplayArtworkNotificationFixtures.artwork;
import static com.example.demo.domain.displayartwork.application.command.DisplayArtworkNotificationFixtures.display;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.displayartwork.application.notification.DisplayMemberNameResolver;
import com.example.demo.domain.displayartwork.application.permission.DisplayArtworkPermissionChecker;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.domain.displayartwork.domain.repository.UserNicknameRepository;
import com.example.demo.domain.displayartwork.domain.type.CreatorRole;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import com.example.demo.domain.notification.application.usecase.RequestNotificationUseCase;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DeleteDisplayArtworkServiceTest {

  private static final Long ARTWORK_ID = 100L;

  private final DisplayArtworkRepository displayArtworkRepository =
      mock(DisplayArtworkRepository.class);
  private final DisplayArtworkCleanupService artworkCleanupService =
      mock(DisplayArtworkCleanupService.class);
  private final CreatorRepository creatorRepository = mock(CreatorRepository.class);
  private final RequestNotificationUseCase requestNotificationUseCase =
      mock(RequestNotificationUseCase.class);
  private final DeleteDisplayArtworkService service =
      new DeleteDisplayArtworkService(
          displayArtworkRepository,
          mock(DisplayArtworkPermissionChecker.class),
          artworkCleanupService,
          creatorRepository,
          requestNotificationUseCase,
          new DisplayMemberNameResolver(mock(UserNicknameRepository.class)));

  @Test
  void deleteNotifiesOtherArtistsAndRegistrantWithDeleterDisplayNickname() {
    DisplayArtwork artwork = artwork(display(), ARTWORK_ID, "얇은 경계", LEADER);
    when(displayArtworkRepository.findById(ARTWORK_ID)).thenReturn(Optional.of(artwork));
    when(creatorRepository.findArtistsByDisplayArtworkIds(List.of(ARTWORK_ID)))
        .thenReturn(
            List.of(
                new Creator(null, "팀원", true, CreatorRole.LEAD_ARTIST, MEMBER, ARTWORK_ID),
                new Creator(
                    null, "다른 팀원", false, CreatorRole.CO_AUTHOR, OTHER_MEMBER, ARTWORK_ID)));

    service.delete(OTHER_MEMBER, ARTWORK_ID);

    verify(artworkCleanupService).delete(artwork);
    // 삭제한 본인(OTHER_MEMBER)은 요청 창구에서 actor로 제외된다.
    verify(requestNotificationUseCase)
        .request(
            argThat(
                recipients ->
                    new HashSet<>(recipients).equals(Set.of(MEMBER, OTHER_MEMBER, LEADER))),
            eq(OTHER_MEMBER),
            eq(NotificationMessages.artworkDeleted("얇은 경계", "다른 팀원")));
  }
}
