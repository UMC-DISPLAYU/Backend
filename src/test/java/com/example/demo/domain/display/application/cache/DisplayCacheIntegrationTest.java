package com.example.demo.domain.display.application.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.application.command.CreateDisplayCommand;
import com.example.demo.domain.display.application.command.CreateDisplayService;
import com.example.demo.domain.display.application.command.UpdateDisplayCommand;
import com.example.demo.domain.display.application.command.UpdateDisplayService;
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
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.repository.DisplayLikeRepository;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.cache.DisplayListCacheEvictor;
import com.example.demo.domain.display.infrastructure.cache.DisplayListCacheVersion;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.global.config.CacheConfig;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringJUnitConfig(
    classes = {
      CacheConfig.class,
      DisplayCacheIntegrationTest.TestConfig.class,
      GetRandomGraduationDisplaysService.class,
      GetDuPicksService.class,
      GetClosingSoonDisplaysService.class,
      CreateDisplayService.class,
      UpdateDisplayService.class,
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
  @Autowired private CreateDisplayService createDisplayService;
  @Autowired private UpdateDisplayService updateDisplayService;
  @Autowired private DisplayRepository displayRepository;
  @Autowired private DisplayLikeRepository displayLikeRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private DisplayListCacheEvictor displayListCacheEvictor;
  @Autowired private CacheManager cacheManager;
  @Autowired private TestClock clock;

  @BeforeEach
  void setUp() {
    reset(
        graduationDisplayQueryRepository,
        duPickQueryRepository,
        closingSoonDisplayQueryRepository,
        displayRepository,
        displayLikeRepository,
        userRepository);
    clearCache(DisplayCacheNames.GRADUATION);
    clearCache(DisplayCacheNames.DU_PICKS);
    clearCache(DisplayCacheNames.CLOSING_SOON_FIRST_PAGE);
    clock.setInstant(Instant.parse("2026-07-13T00:00:00Z"));
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
  void graduationDisplaysCacheSameSizeRequestSeparatelyByDate() {
    when(graduationDisplayQueryRepository.findRandomGraduationDisplays(5))
        .thenReturn(
            List.of(displayQueryResult(1L, LocalDate.of(2026, 7, 20))),
            List.of(displayQueryResult(2L, LocalDate.of(2026, 7, 21))));

    graduationDisplaysService.getRandomGraduationDisplays(5);
    graduationDisplaysService.getRandomGraduationDisplays(5);
    clock.setInstant(Instant.parse("2026-07-14T00:00:00Z"));
    graduationDisplaysService.getRandomGraduationDisplays(5);

    verify(graduationDisplayQueryRepository, times(2)).findRandomGraduationDisplays(5);
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
  void closingSoonFirstPageCacheSameSizeRequestSeparatelyByDate() {
    when(closingSoonDisplayQueryRepository.findClosingSoonDisplays(
            any(ClosingSoonDisplayQuery.class), any(LocalDate.class), eq(2)))
        .thenReturn(
            List.of(
                displayQueryResult(1L, LocalDate.of(2026, 7, 20)),
                displayQueryResult(2L, LocalDate.of(2026, 7, 21))),
            List.of(
                displayQueryResult(3L, LocalDate.of(2026, 7, 22)),
                displayQueryResult(4L, LocalDate.of(2026, 7, 23))));

    closingSoonDisplaysService.getClosingSoonDisplays(new ClosingSoonDisplayQuery(null, 1));
    closingSoonDisplaysService.getClosingSoonDisplays(new ClosingSoonDisplayQuery(null, 1));
    clock.setInstant(Instant.parse("2026-07-14T00:00:00Z"));
    closingSoonDisplaysService.getClosingSoonDisplays(new ClosingSoonDisplayQuery(null, 1));

    verify(closingSoonDisplayQueryRepository, times(2))
        .findClosingSoonDisplays(any(ClosingSoonDisplayQuery.class), any(LocalDate.class), eq(2));
  }

  @Test
  void createDisplayEvictsDisplayListCachesAfterCommit() {
    seedDisplayListCaches();
    when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().name("팀장").build()));
    when(displayRepository.save(any(Display.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    createDisplayService.createDisplay(createDisplayCommand());

    assertDisplayListCachesEmpty();
  }

  @Test
  void updateDisplayEvictsDisplayListCachesAfterCommit() {
    Display display = displayWithTeamLeader();
    seedDisplayListCaches();
    when(displayRepository.findById(1L)).thenReturn(Optional.of(display));
    when(displayLikeRepository.countByDisplayIdAndDeletedAtIsNull(display.getId())).thenReturn(0L);

    updateDisplayService.updateDisplay(updateDisplayCommand());

    assertDisplayListCachesEmpty();
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

  private void seedDisplayListCaches() {
    cacheManager.getCache(DisplayCacheNames.GRADUATION).put(5, "graduation");
    cacheManager.getCache(DisplayCacheNames.DU_PICKS).put(new DuPickQuery(null, 1), "duPicks");
    cacheManager.getCache(DisplayCacheNames.CLOSING_SOON_FIRST_PAGE).put(1, "closingSoon");
  }

  private void assertDisplayListCachesEmpty() {
    assertThat(cacheManager.getCache(DisplayCacheNames.GRADUATION).get(5)).isNull();
    assertThat(cacheManager.getCache(DisplayCacheNames.DU_PICKS).get(new DuPickQuery(null, 1)))
        .isNull();
    assertThat(cacheManager.getCache(DisplayCacheNames.CLOSING_SOON_FIRST_PAGE).get(1)).isNull();
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

  private static CreateDisplayCommand createDisplayCommand() {
    return new CreateDisplayCommand(
        1L,
        "전시",
        "https://cdn.displayu.com/posters/main.png",
        "부제",
        "설명",
        "전시장",
        BigDecimal.valueOf(37.5513),
        BigDecimal.valueOf(126.9248),
        "서울시 동작구",
        "",
        "유의사항",
        "중앙대학교",
        "디자인학부",
        "전시 리더",
        null,
        DisplayType.GRADUATION,
        List.of(DisplayField.DESIGN),
        DisplayRegion.SEOUL,
        LocalDate.of(2026, 5, 28),
        LocalDate.of(2026, 6, 5),
        LocalTime.of(10, 0),
        LocalTime.of(18, 0),
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.ON_EXHIBITION);
  }

  private static UpdateDisplayCommand updateDisplayCommand() {
    return new UpdateDisplayCommand(
        1L,
        1L,
        "수정 전시",
        null,
        DisplayType.GRADUATION,
        List.of(DisplayField.DESIGN),
        "중앙대학교",
        "디자인학부",
        null,
        null,
        "수정 부제",
        "수정 설명",
        LocalDate.of(2026, 5, 29),
        LocalDate.of(2026, 6, 6),
        LocalTime.of(11, 0),
        LocalTime.of(19, 0),
        "수정 전시장",
        "수정 유의사항");
  }

  private static Display displayWithTeamLeader() {
    Display display =
        Display.create(
            new UserId(1L),
            "전시",
            "https://cdn.displayu.com/posters/main.png",
            "부제",
            "설명",
            new DisplayLocation("전시장", BigDecimal.valueOf(37.5513), BigDecimal.valueOf(126.9248)),
            "",
            "유의사항",
            "중앙대학교",
            "디자인학부",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 5, 28),
                LocalDate.of(2026, 6, 5),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.ON_EXHIBITION);
    display.addTeamMember(
        new TeamMember(null, new UserId(1L), "팀장", TeamMemberRole.TEAM_LEADER, true));
    return display;
  }

  @Configuration
  @EnableTransactionManagement(proxyTargetClass = true)
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
    DisplayRepository displayRepository() {
      return mock(DisplayRepository.class);
    }

    @Bean
    DisplayLikeRepository displayLikeRepository() {
      return mock(DisplayLikeRepository.class);
    }

    @Bean
    UserRepository userRepository() {
      return mock(UserRepository.class);
    }

    @Bean
    PlatformTransactionManager transactionManager() {
      return new AbstractPlatformTransactionManager() {
        @Override
        protected Object doGetTransaction() {
          return new Object();
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {}

        @Override
        protected void doCommit(DefaultTransactionStatus status) {}

        @Override
        protected void doRollback(DefaultTransactionStatus status) {}
      };
    }

    @Bean
    TestClock clock() {
      return new TestClock(Instant.parse("2026-07-13T00:00:00Z"), ZoneId.of("Asia/Seoul"));
    }
  }

  private static class TestClock extends Clock {

    private Instant instant;
    private final ZoneId zone;

    private TestClock(Instant instant, ZoneId zone) {
      this.instant = instant;
      this.zone = zone;
    }

    private void setInstant(Instant instant) {
      this.instant = instant;
    }

    @Override
    public ZoneId getZone() {
      return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
      return new TestClock(instant, zone);
    }

    @Override
    public Instant instant() {
      return instant;
    }
  }
}
