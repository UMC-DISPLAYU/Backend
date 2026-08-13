package com.example.demo.domain.display.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.result.MyDisplayListResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetArtistDisplaysServiceTest {

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2026-08-12T00:00:00Z"), ZoneOffset.UTC);

  @Test
  void getArtistDisplaysUsesPublishedDisplayQueries() {
    FakeDisplayRepository repository =
        new FakeDisplayRepository(
            List.of(display(1L, "만든 전시", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 20))),
            List.of(display(2L, "참여 전시", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 10))));
    GetArtistDisplaysService service = new GetArtistDisplaysService(repository, CLOCK);

    MyDisplayListResult result = service.getArtistDisplays(1L);

    assertThat(repository.publishedCreatedUserId).isEqualTo(1L);
    assertThat(repository.publishedParticipatedUserId).isEqualTo(1L);
    assertThat(result.createdDisplays()).hasSize(1);
    assertThat(result.createdDisplays().getFirst().title()).isEqualTo("만든 전시");
    assertThat(result.createdDisplays().getFirst().displayStatus()).isEqualTo("DISPLAYING");
    assertThat(result.createdDisplays().getFirst().postImageUrl())
        .isEqualTo("https://cdn.displayu.com/posters/main.png");
    assertThat(result.participatedDisplays()).hasSize(1);
    assertThat(result.participatedDisplays().getFirst().displayStatus()).isEqualTo("ENDED");
  }

  @Test
  void getArtistDisplaysReturnsUpcomingBeforeStartTimeOnStartDate() {
    Clock clock = Clock.fixed(Instant.parse("2026-08-11T16:00:00Z"), ZoneId.of("Asia/Seoul"));
    FakeDisplayRepository repository =
        new FakeDisplayRepository(
            List.of(
                display(
                    1L,
                    "오픈 전 전시",
                    LocalDate.of(2026, 8, 12),
                    LocalDate.of(2026, 8, 20),
                    LocalTime.of(9, 0),
                    LocalTime.of(18, 0))),
            List.of());
    GetArtistDisplaysService service = new GetArtistDisplaysService(repository, clock);

    MyDisplayListResult result = service.getArtistDisplays(1L);

    assertThat(result.createdDisplays().getFirst().displayStatus()).isEqualTo("UPCOMING");
  }

  private static Display display(
      Long ownerUserId, String title, LocalDate startDate, LocalDate endDate) {
    return display(
        ownerUserId, title, startDate, endDate, LocalTime.of(10, 0), LocalTime.of(18, 0));
  }

  private static Display display(
      Long ownerUserId,
      String title,
      LocalDate startDate,
      LocalDate endDate,
      LocalTime startTime,
      LocalTime endTime) {
    return Display.create(
        new UserId(ownerUserId),
        title,
        "https://cdn.displayu.com/posters/main.png",
        "subtitle",
        "content",
        new DisplayLocation("디유 갤러리", bd("37.5513"), bd("126.9248")),
        "",
        "",
        "디유대학교",
        "디자인학부",
        DisplayType.GRADUATION,
        List.of(DisplayField.DESIGN),
        new DisplayPeriod(startDate, endDate, startTime, endTime),
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.ON_EXHIBITION);
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }

  private static class FakeDisplayRepository implements DisplayRepository {

    private final List<Display> publishedCreatedDisplays;
    private final List<Display> publishedParticipatedDisplays;
    private Long publishedCreatedUserId;
    private Long publishedParticipatedUserId;

    private FakeDisplayRepository(
        List<Display> publishedCreatedDisplays, List<Display> publishedParticipatedDisplays) {
      this.publishedCreatedDisplays = publishedCreatedDisplays;
      this.publishedParticipatedDisplays = publishedParticipatedDisplays;
    }

    @Override
    public Optional<Display> findById(Long displayId) {
      return Optional.empty();
    }

    @Override
    public Optional<Display> findByIdWithOptimisticLock(Long displayId) {
      return Optional.empty();
    }

    @Override
    public Optional<Display> findByInvitationToken(String invitationTokenHash) {
      return Optional.empty();
    }

    @Override
    public List<Display> findCreatedDisplaysByUserId(Long userId) {
      return List.of();
    }

    @Override
    public List<Display> findParticipatedDisplaysByUserId(Long userId) {
      return List.of();
    }

    @Override
    public List<Display> findPublishedCreatedDisplaysByUserId(Long userId) {
      publishedCreatedUserId = userId;
      return publishedCreatedDisplays;
    }

    @Override
    public List<Display> findPublishedParticipatedDisplaysByUserId(Long userId) {
      publishedParticipatedUserId = userId;
      return publishedParticipatedDisplays;
    }

    @Override
    public List<Display> findAll() {
      return List.of();
    }

    @Override
    public boolean existsByOwnerUserIdAndTitle(Long ownerUserId, String title) {
      return false;
    }

    @Override
    public Display save(Display display) {
      return display;
    }

    @Override
    public void flush() {}

    @Override
    public void delete(Display display) {}
  }
}
