package com.example.demo.domain.display.application.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.application.port.DisplayListCacheEvictionPort;
import com.example.demo.domain.display.application.service.DisplayContentPublicationService;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.repository.DisplayScreeningRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DisplayScreeningCommandServiceTest {

  private static final Instant NOW = Instant.parse("2026-09-20T01:00:00Z");
  private final DisplayRepository displayRepository = mock(DisplayRepository.class);
  private final DisplayScreeningRepository screeningRepository =
      mock(DisplayScreeningRepository.class);
  private final DisplayContentPublicationService publicationService =
      mock(DisplayContentPublicationService.class);
  private final DisplayListCacheEvictionPort cacheEvictionPort =
      mock(DisplayListCacheEvictionPort.class);
  private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);

  @Test
  void approvePublishesContentsAndEvictsListCache() {
    Display display = mock(Display.class);
    DisplayScreening screening = mock(DisplayScreening.class);
    when(displayRepository.findByIdWithOptimisticLock(1L)).thenReturn(Optional.of(display));
    when(screeningRepository.findLatestByDisplayId(1L)).thenReturn(Optional.of(screening));

    new ApproveDisplayScreeningService(
            displayRepository, screeningRepository, publicationService, cacheEvictionPort, clock)
        .approve(1L, 7L);

    verify(display).approveReview();
    verify(screening).approve(7L, LocalDateTime.ofInstant(NOW, ZoneOffset.UTC));
    verify(publicationService).publishForDisplay(1L);
    verify(cacheEvictionPort).evictAfterCommit();
  }

  @Test
  void rejectDoesNotPublishContentsOrEvictPublicListCache() {
    Display display = mock(Display.class);
    DisplayScreening screening = mock(DisplayScreening.class);
    when(displayRepository.findByIdWithOptimisticLock(1L)).thenReturn(Optional.of(display));
    when(screeningRepository.findLatestByDisplayId(1L)).thenReturn(Optional.of(screening));

    new RejectDisplayScreeningService(displayRepository, screeningRepository, clock)
        .reject(1L, 7L, "일정 수정");

    verify(display).rejectReview();
    verify(screening).reject(7L, "일정 수정", LocalDateTime.ofInstant(NOW, ZoneOffset.UTC));
    verifyNoInteractions(publicationService, cacheEvictionPort);
  }
}
