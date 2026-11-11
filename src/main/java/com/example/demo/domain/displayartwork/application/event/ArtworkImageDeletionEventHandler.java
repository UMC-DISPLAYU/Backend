package com.example.demo.domain.displayartwork.application.event;

import com.example.demo.domain.displayartwork.application.port.ArtworkImageDeletionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ArtworkImageDeletionEventHandler {

  private static final Logger log = LoggerFactory.getLogger(ArtworkImageDeletionEventHandler.class);
  private static final int MAX_RETRY_COUNT = 3;

  private final ArtworkImageDeletionPort artworkImageDeletionPort;

  public ArtworkImageDeletionEventHandler(ArtworkImageDeletionPort artworkImageDeletionPort) {
    this.artworkImageDeletionPort = artworkImageDeletionPort;
  }

  @Async("artworkImageCleanupExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ArtworkImagesDeletedEvent event) {
    for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
      try {
        // DB 커밋 뒤에만 S3를 지워, 트랜잭션 롤백과 파일 삭제가 엇갈리지 않게 한다.
        artworkImageDeletionPort.deleteAll(event.imageUrls());
        return;
      } catch (RuntimeException exception) {
        log.warn(
            "Failed to delete artwork images. attempt={}/{} imageCount={}",
            attempt,
            MAX_RETRY_COUNT,
            event.imageUrls().size(),
            exception);
      }
    }
    // 즉시 재시도가 모두 실패해도 DB 삭제는 유지하며 운영 로그를 근거로 재처리한다.
    log.error("Artwork image deletion exhausted retries. imageUrls={}", event.imageUrls());
  }
}
