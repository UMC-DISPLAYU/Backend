package com.example.demo.domain.display.application.command;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.display.domain.repository.DisplayLikeRepository;
import org.junit.jupiter.api.Test;

class CleanupDeletedDisplayServiceTest {

  private final DisplayLikeRepository displayLikeRepository =
      org.mockito.Mockito.mock(DisplayLikeRepository.class);
  private final CleanupDeletedDisplayService service =
      new CleanupDeletedDisplayService(displayLikeRepository);

  @Test
  void deletesOnlyDisplayOwnedLikesAndRemainsIdempotentWhenRepeated() {
    service.cleanup(10L);
    service.cleanup(10L);

    verify(displayLikeRepository, times(2)).deleteAllByDisplayId(10L);
  }
}
