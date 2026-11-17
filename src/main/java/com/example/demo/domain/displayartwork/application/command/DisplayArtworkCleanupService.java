package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import com.example.demo.domain.notification.application.usecase.RequestNotificationUseCase;
import com.example.demo.global.file.application.event.MediaFilesRemovedEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/** 사용자와 작가로 연결된 전시 작품을 정리한다. 전시 탈퇴뿐 아니라 향후 회원 탈퇴에서도 재사용할 수 있다. */
@Service
public class DisplayArtworkCleanupService {

  private final DisplayArtworkRepository displayArtworkRepository;
  private final CreatorRepository creatorRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final RequestNotificationUseCase requestNotificationUseCase;

  public DisplayArtworkCleanupService(
      DisplayArtworkRepository displayArtworkRepository,
      CreatorRepository creatorRepository,
      ApplicationEventPublisher eventPublisher,
      RequestNotificationUseCase requestNotificationUseCase) {
    this.displayArtworkRepository = displayArtworkRepository;
    this.creatorRepository = creatorRepository;
    this.eventPublisher = eventPublisher;
    this.requestNotificationUseCase = requestNotificationUseCase;
  }

  /**
   * 작가 계정이 전시에서 나가거나 내보내질 때 그 계정이 작가로 연결된 작품을 삭제하고, 같은 작품에 남은 작가·등록자에게 받는 사람당 한 건씩 알린다.
   *
   * @param artistUserId 전시에서 빠지는 작가 계정
   * @param actorUserId 이 일을 일으킨 사람(직접 나간 경우 본인, 내보낸 경우 대표자). 이 사람에게는 알리지 않는다
   */
  public int deleteByArtistAccount(Long displayId, Long artistUserId, Long actorUserId) {
    List<DisplayArtwork> artworks =
        displayArtworkRepository.findAllActiveByDisplayIdAndArtistUserId(displayId, artistUserId);
    if (artworks.isEmpty()) {
      return 0;
    }

    // 작품이 삭제되기 전에 받는 사람별로 삭제될 작품명을 모아 둔다.
    Map<Long, List<String>> artworkNamesByRecipient =
        collectArtworkNamesByRecipient(artworks, artistUserId);

    // 등록자가 아니라 Creator.userId가 연결된 작품 전체를 삭제한다. 공동 작품도 예외로 두지 않는다.
    artworks.forEach(this::delete);

    String displayTitle = artworks.get(0).getDisplay().getTitle();
    artworkNamesByRecipient.forEach(
        (recipientUserId, artworkNames) ->
            requestNotificationUseCase.request(
                List.of(recipientUserId),
                actorUserId,
                NotificationMessages.artworksDeletedByArtistLeave(artworkNames, displayTitle)));
    return artworks.size();
  }

  public void delete(DisplayArtwork artwork) {
    List<String> fileUrls = artwork.getMediaFileUrls();
    artwork.delete();
    if (!fileUrls.isEmpty()) {
      eventPublisher.publishEvent(new MediaFilesRemovedEvent(fileUrls));
    }
  }

  /** 받는 사람은 작품의 다른 작가(QnA 담당 전용 제외)와 등록자이고, 빠지는 작가 본인은 제외한다. */
  private Map<Long, List<String>> collectArtworkNamesByRecipient(
      List<DisplayArtwork> artworks, Long artistUserId) {
    Map<Long, List<Long>> artistUserIdsByArtworkId =
        creatorRepository
            .findArtistsByDisplayArtworkIds(artworks.stream().map(DisplayArtwork::getId).toList())
            .stream()
            .filter(creator -> creator.getUserId() != null)
            .collect(
                Collectors.groupingBy(
                    Creator::getDisplayArtworkId,
                    Collectors.mapping(Creator::getUserId, Collectors.toList())));

    Map<Long, List<String>> artworkNamesByRecipient = new LinkedHashMap<>();
    for (DisplayArtwork artwork : artworks) {
      Set<Long> recipients =
          new LinkedHashSet<>(artistUserIdsByArtworkId.getOrDefault(artwork.getId(), List.of()));
      if (artwork.getRegisteredByUserId() != null) {
        recipients.add(artwork.getRegisteredByUserId());
      }
      recipients.remove(artistUserId);
      for (Long recipient : recipients) {
        artworkNamesByRecipient
            .computeIfAbsent(recipient, key -> new ArrayList<>())
            .add(artwork.getArtworkName());
      }
    }
    return artworkNamesByRecipient;
  }
}
