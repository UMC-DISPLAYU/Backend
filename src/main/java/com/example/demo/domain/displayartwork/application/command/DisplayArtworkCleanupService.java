package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.displayartwork.application.event.ArtworkImagesDeletedEvent;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/** 사용자와 작가로 연결된 전시 작품을 정리한다. 전시 탈퇴뿐 아니라 향후 회원 탈퇴에서도 재사용할 수 있다. */
@Service
public class DisplayArtworkCleanupService {

  private final DisplayArtworkRepository displayArtworkRepository;
  private final ApplicationEventPublisher eventPublisher;

  public DisplayArtworkCleanupService(
      DisplayArtworkRepository displayArtworkRepository, ApplicationEventPublisher eventPublisher) {
    this.displayArtworkRepository = displayArtworkRepository;
    this.eventPublisher = eventPublisher;
  }

  public int deleteByArtistAccount(Long displayId, Long userId) {
    List<DisplayArtwork> artworks =
        displayArtworkRepository.findAllActiveByDisplayIdAndArtistUserId(displayId, userId);

    // 등록자가 아니라 Creator.userId가 연결된 작품 전체를 삭제한다. 공동 작품도 예외로 두지 않는다.
    artworks.forEach(this::delete);
    return artworks.size();
  }

  public void delete(DisplayArtwork artwork) {
    List<String> imageUrls = artwork.getImages().stream().map(ArtworkImage::getImageUrl).toList();
    artwork.delete();
    if (!imageUrls.isEmpty()) {
      eventPublisher.publishEvent(new ArtworkImagesDeletedEvent(imageUrls));
    }
  }
}
