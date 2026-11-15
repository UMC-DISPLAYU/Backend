package com.example.demo.global.file.application.event;

import com.example.demo.global.file.application.port.UnusedMediaDeletionPort;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MediaFileCleanupEventHandler {
  private static final Logger log = LoggerFactory.getLogger(MediaFileCleanupEventHandler.class);
  private static final int MAX_ATTEMPTS = 3;
  private final UnusedMediaDeletionPort deletionPort;

  public MediaFileCleanupEventHandler(UnusedMediaDeletionPort deletionPort) {
    this.deletionPort = deletionPort;
  }

  @Async("mediaFileCleanupExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(MediaFilesRemovedEvent event) {
    if (event.fileUrls().isEmpty()) {
      return;
    }
    for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
      try {
        deletionPort.deleteAll(event.fileUrls());
        return;
      } catch (RuntimeException exception) {
        log.warn(
            "Failed to delete unused media. attempt={}/{} fileCount={}",
            attempt,
            MAX_ATTEMPTS,
            event.fileUrls().size(),
            exception);
      }
    }
    log.error(
        "Unused media deletion exhausted retries. fileUrls={}",
        event.fileUrls().stream().map(MediaFileCleanupEventHandler::safeLogUrl).toList());
  }

  private static String safeLogUrl(String value) {
    try {
      URI uri = URI.create(value);
      if (uri.getHost() == null) {
        return "<invalid URL>";
      }
      return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), null, null)
          .toString();
    } catch (IllegalArgumentException | URISyntaxException exception) {
      return "<invalid URL>";
    }
  }
}
