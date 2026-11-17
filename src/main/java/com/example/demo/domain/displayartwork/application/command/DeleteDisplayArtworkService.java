package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.displayartwork.application.notification.DisplayMemberNameResolver;
import com.example.demo.domain.displayartwork.application.permission.DisplayArtworkPermissionChecker;
import com.example.demo.domain.displayartwork.application.result.DeleteDisplayArtworkResult;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import com.example.demo.domain.notification.application.usecase.RequestNotificationUseCase;
import com.example.demo.global.error.BusinessException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteDisplayArtworkService {

  private final DisplayArtworkRepository displayArtworkRepository;
  private final DisplayArtworkPermissionChecker permissionChecker;
  private final DisplayArtworkCleanupService artworkCleanupService;
  private final CreatorRepository creatorRepository;
  private final RequestNotificationUseCase requestNotificationUseCase;
  private final DisplayMemberNameResolver displayMemberNameResolver;

  public DeleteDisplayArtworkService(
      DisplayArtworkRepository displayArtworkRepository,
      DisplayArtworkPermissionChecker permissionChecker,
      DisplayArtworkCleanupService artworkCleanupService,
      CreatorRepository creatorRepository,
      RequestNotificationUseCase requestNotificationUseCase,
      DisplayMemberNameResolver displayMemberNameResolver) {
    this.displayArtworkRepository = displayArtworkRepository;
    this.permissionChecker = permissionChecker;
    this.artworkCleanupService = artworkCleanupService;
    this.creatorRepository = creatorRepository;
    this.requestNotificationUseCase = requestNotificationUseCase;
    this.displayMemberNameResolver = displayMemberNameResolver;
  }

  @Transactional
  public DeleteDisplayArtworkResult delete(Long requesterUserId, Long artworkId) {
    DisplayArtwork artwork =
        displayArtworkRepository
            .findById(artworkId)
            .filter(a -> !a.isDeleted() && !a.getDisplay().isDeleted())
            .orElseThrow(
                () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));

    // 전시 대표자, 작품의 작가, 공동 작업자만 삭제할 수 있다.
    permissionChecker.requireArtworkDeleter(requesterUserId, artwork.getDisplay(), artworkId);

    artworkCleanupService.delete(artwork);
    requestDeletedNotification(requesterUserId, artwork);

    return new DeleteDisplayArtworkResult(artworkId, "작품이 성공적으로 삭제되었습니다.");
  }

  /** 같은 작품의 다른 작가(QnA 담당 전용 제외)와 등록자에게 알린다. 삭제한 본인은 제외된다. */
  private void requestDeletedNotification(Long requesterUserId, DisplayArtwork artwork) {
    Set<Long> recipients = new LinkedHashSet<>();
    creatorRepository.findArtistsByDisplayArtworkIds(List.of(artwork.getId())).stream()
        .map(Creator::getUserId)
        .forEach(recipients::add);
    recipients.add(artwork.getRegisteredByUserId());

    requestNotificationUseCase.request(
        recipients,
        requesterUserId,
        NotificationMessages.artworkDeleted(
            artwork.getArtworkName(),
            displayMemberNameResolver.resolve(artwork.getDisplay(), requesterUserId)));
  }
}
