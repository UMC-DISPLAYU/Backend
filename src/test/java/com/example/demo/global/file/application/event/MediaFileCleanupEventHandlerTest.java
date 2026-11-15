package com.example.demo.global.file.application.event;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.demo.global.file.application.port.UnusedMediaDeletionPort;
import java.util.List;
import org.junit.jupiter.api.Test;

class MediaFileCleanupEventHandlerTest {

  private final UnusedMediaDeletionPort deletionPort =
      org.mockito.Mockito.mock(UnusedMediaDeletionPort.class);
  private final MediaFileCleanupEventHandler handler =
      new MediaFileCleanupEventHandler(deletionPort);

  @Test
  void retriesS3DeletionThreeTimesWhenItKeepsFailing() {
    List<String> imageUrls = List.of("https://cdn.displayu.com/artworks/1.png");
    doThrow(new IllegalStateException("S3 unavailable")).when(deletionPort).deleteAll(imageUrls);

    handler.handle(new MediaFilesRemovedEvent(imageUrls));

    verify(deletionPort, times(3)).deleteAll(imageUrls);
  }

  @Test
  void stopsRetryingAfterS3DeletionSucceeds() {
    List<String> imageUrls = List.of("https://cdn.displayu.com/artworks/1.png");

    handler.handle(new MediaFilesRemovedEvent(imageUrls));

    verify(deletionPort).deleteAll(imageUrls);
  }

  @Test
  void succeedsOnSecondAttemptAndDoesNotRunThird() {
    List<String> urls = List.of("https://cdn.example/images/artwork/cover.png");
    doThrow(new IllegalStateException("temporary failure"))
        .doNothing()
        .when(deletionPort)
        .deleteAll(urls);
    handler.handle(new MediaFilesRemovedEvent(urls));
    verify(deletionPort, times(2)).deleteAll(urls);
  }
}
