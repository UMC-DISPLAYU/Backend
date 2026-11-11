package com.example.demo.domain.displayartwork.application.event;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.displayartwork.application.port.ArtworkImageDeletionPort;
import java.util.List;
import org.junit.jupiter.api.Test;

class ArtworkImageDeletionEventHandlerTest {

  private final ArtworkImageDeletionPort deletionPort =
      org.mockito.Mockito.mock(ArtworkImageDeletionPort.class);
  private final ArtworkImageDeletionEventHandler handler =
      new ArtworkImageDeletionEventHandler(deletionPort);

  @Test
  void retriesS3DeletionThreeTimesWhenItKeepsFailing() {
    List<String> imageUrls = List.of("https://cdn.displayu.com/artworks/1.png");
    doThrow(new IllegalStateException("S3 unavailable")).when(deletionPort).deleteAll(imageUrls);

    handler.handle(new ArtworkImagesDeletedEvent(imageUrls));

    verify(deletionPort, times(3)).deleteAll(imageUrls);
  }

  @Test
  void stopsRetryingAfterS3DeletionSucceeds() {
    List<String> imageUrls = List.of("https://cdn.displayu.com/artworks/1.png");

    handler.handle(new ArtworkImagesDeletedEvent(imageUrls));

    verify(deletionPort).deleteAll(imageUrls);
  }
}
