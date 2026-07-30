package com.example.demo.domain.display.application.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQuery;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQuery.Cursor;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryRepository;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.application.query.DuPickQuery;
import com.example.demo.domain.display.application.query.DuPickQueryRepository;
import com.example.demo.domain.display.application.query.DuPickQueryResult;
import com.example.demo.domain.display.application.query.GraduationDisplayQueryRepository;
import com.example.demo.domain.display.application.result.GraduationDisplayResult;
import com.example.demo.domain.display.application.result.GraduationDisplayResult.ExhibitionResult;
import com.example.demo.domain.display.application.service.GetClosingSoonDisplaysService;
import com.example.demo.domain.display.application.service.GetDuPicksService;
import com.example.demo.domain.display.application.service.GetRandomGraduationDisplaysService;
import com.example.demo.domain.display.application.usecase.GetClosingSoonDisplaysUseCase;
import com.example.demo.domain.display.application.usecase.GetDuPicksUseCase;
import com.example.demo.domain.display.application.usecase.GetRandomGraduationDisplaysUseCase;
import com.example.demo.domain.display.infrastructure.cache.DisplayListCacheEvictor;
import com.example.demo.domain.display.infrastructure.cache.DisplayListCacheVersion;
import com.example.demo.global.config.CacheConfig;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringJUnitConfig(
    classes = {
      CacheConfig.class,
      DisplayCacheIntegrationTest.TestConfig.class,
      GetRandomGraduationDisplaysService.class,
      GetDuPicksService.class,
      GetClosingSoonDisplaysService.class,
      DisplayListCacheVersion.class,
      DisplayListCacheEvictor.class
    })
class DisplayCacheIntegrationTest {

  @Autowired private GraduationDisplayQueryRepository graduationDisplayQueryRepository;
  @Autowired private DuPickQueryRepository duPickQueryRepository;
  @Autowired private ClosingSoonDisplayQueryRepository closingSoonDisplayQueryRepository;
  @Autowired private GetRandomGraduationDisplaysUseCase graduationDisplaysService;
  @Autowired private GetDuPicksUseCase duPicksService;
  @Autowired private GetClosingSoonDisplaysUseCase closingSoonDisplaysService;
  @Autowired private DisplayListCacheEvictor displayListCacheEvictor;
  @Autowired private CacheManager cacheManager;

  @BeforeEach
  void setUp() {
    reset(
        graduationDisplayQueryRepository, duPickQueryRepository, closingSoonDisplayQueryRepository);
    clearCache(DisplayCacheNames.GRADUATION);
    clearCache(DisplayCacheNames.DU_PICKS);
    clearCache(DisplayCacheNames.CLOSING_SOON_FIRST_PAGE);
  }

  @Test
  void graduationDisplaysCacheSameSizeRequest() {
    when(graduationDisplayQueryRepository.findRandomGraduationDisplays(5))
        .thenReturn(List.of(displayQueryResult(1L, LocalDate.of(2026, 7, 20))));

    graduationDisplaysService.getRandomGraduationDisplays(5);
    graduationDisplaysService.getRandomGraduationDisplays(5);

    verify(graduationDisplayQueryRepository, times(1)).findRandomGraduationDisplays(5);
  }

  @Test
  void duPicksCacheSameCursorAndSizeRequest() {
    DuPickQuery query = new DuPickQuery(null, 1);
    when(duPickQueryRepository.findDuPicks(query, 2))
        .thenReturn(List.of(duPickQueryResult(1L), duPickQueryResult(2L)));

    duPicksService.getDuPicks(query);
    duPicksService.getDuPicks(new DuPickQuery(null, 1));

    verify(duPickQueryRepository, times(1)).findDuPicks(query, 2);
  }

  @Test
  void closingSoonCachesOnlyFirstPage() {
    when(closingSoonDisplayQueryRepository.findClosingSoonDisplays(
            any(ClosingSoonDisplayQuery.class), any(LocalDate.class), eq(2)))
        .thenReturn(
            List.of(
                displayQueryResult(1L, LocalDate.of(2026, 7, 20)),
                displayQueryResult(2L, LocalDate.of(2026, 7, 21))));

    closingSoonDisplaysService.getClosingSoonDisplays(new ClosingSoonDisplayQuery(null, 1));
    closingSoonDisplaysService.getClosingSoonDisplays(new ClosingSoonDisplayQuery(null, 1));
    closingSoonDisplaysService.getClosingSoonDisplays(
        new ClosingSoonDisplayQuery(new Cursor(LocalDate.of(2026, 7, 20), 1L), 1));
    closingSoonDisplaysService.getClosingSoonDisplays(
        new ClosingSoonDisplayQuery(new Cursor(LocalDate.of(2026, 7, 20), 1L), 1));

    verify(closingSoonDisplayQueryRepository, times(3))
        .findClosingSoonDisplays(any(ClosingSoonDisplayQuery.class), any(LocalDate.class), eq(2));
  }

  @Test
  void displayListCachesEvictAfterCommit() {
    cacheManager.getCache(DisplayCacheNames.GRADUATION).put(5, "graduation");
    cacheManager.getCache(DisplayCacheNames.DU_PICKS).put(new DuPickQuery(null, 1), "duPicks");
    cacheManager.getCache(DisplayCacheNames.CLOSING_SOON_FIRST_PAGE).put(1, "closingSoon");

    TransactionSynchronizationManager.initSynchronization();
    try {
      displayListCacheEvictor.evictAfterCommit();

      assertThat(cacheManager.getCache(DisplayCacheNames.GRADUATION).get(5)).isNotNull();
      TransactionSynchronizationManager.getSynchronizations().stream()
          .forEach(TransactionSynchronization::afterCommit);
    } finally {
      TransactionSynchronizationManager.clearSynchronization();
    }

    assertThat(cacheManager.getCache(DisplayCacheNames.GRADUATION).get(5)).isNull();
    assertThat(cacheManager.getCache(DisplayCacheNames.DU_PICKS).get(new DuPickQuery(null, 1)))
        .isNull();
    assertThat(cacheManager.getCache(DisplayCacheNames.CLOSING_SOON_FIRST_PAGE).get(1)).isNull();
  }

  @Test
  void displayListCachesIgnoreStaleReadCompletedAfterCommitEviction() throws Exception {
    CountDownLatch staleReadStarted = new CountDownLatch(1);
    CountDownLatch commitEvicted = new CountDownLatch(1);
    AtomicInteger callCount = new AtomicInteger();
    when(graduationDisplayQueryRepository.findRandomGraduationDisplays(5))
        .thenAnswer(
            invocation -> {
              if (callCount.incrementAndGet() == 1) {
                staleReadStarted.countDown();
                assertThat(commitEvicted.await(1, TimeUnit.SECONDS)).isTrue();
                return List.of(displayQueryResult(1L, LocalDate.of(2026, 7, 20)));
              }
              return List.of(displayQueryResult(2L, LocalDate.of(2026, 7, 21)));
            });

    Thread staleRead = new Thread(() -> graduationDisplaysService.getRandomGraduationDisplays(5));
    staleRead.start();
    assertThat(staleReadStarted.await(1, TimeUnit.SECONDS)).isTrue();

    TransactionSynchronizationManager.initSynchronization();
    try {
      displayListCacheEvictor.evictAfterCommit();
      TransactionSynchronizationManager.getSynchronizations().stream()
          .forEach(TransactionSynchronization::afterCommit);
    } finally {
      TransactionSynchronizationManager.clearSynchronization();
    }
    commitEvicted.countDown();
    staleRead.join(1000);
    assertThat(staleRead.isAlive()).isFalse();

    GraduationDisplayResult result = graduationDisplaysService.getRandomGraduationDisplays(5);

    assertThat(result.exhibitions()).extracting(ExhibitionResult::displayId).containsExactly(2L);
    verify(graduationDisplayQueryRepository, times(2)).findRandomGraduationDisplays(5);
  }

  private void clearCache(String cacheName) {
    cacheManager.getCache(cacheName).clear();
  }

  private static ClosingSoonDisplayQueryResult displayQueryResult(
      Long displayId, LocalDate endedAt) {
    return new ClosingSoonDisplayQueryResult(
        displayId,
        "전시",
        "https://cdn.displayu.com/posters/main.png",
        "organization",
        "department",
        endedAt.minusDays(7),
        endedAt);
  }

  private static DuPickQueryResult duPickQueryResult(Long duPickId) {
    return new DuPickQueryResult(
        duPickId,
        "THE ESSENCE IN MOTION",
        "색과 형태, 우리가 마주한 순간들",
        "https://cdn.displayu.com/home/du_pick_1.png",
        LocalDateTime.of(2026, 6, 30, 11, 0));
  }

  @Configuration
  static class TestConfig {

    @Bean
    GraduationDisplayQueryRepository graduationDisplayQueryRepository() {
      return mock(GraduationDisplayQueryRepository.class);
    }

    @Bean
    DuPickQueryRepository duPickQueryRepository() {
      return mock(DuPickQueryRepository.class);
    }

    @Bean
    ClosingSoonDisplayQueryRepository closingSoonDisplayQueryRepository() {
      return mock(ClosingSoonDisplayQueryRepository.class);
    }

    @Bean
    Clock clock() {
      return Clock.fixed(Instant.parse("2026-07-13T00:00:00Z"), ZoneId.of("Asia/Seoul"));
    }
  }
}
