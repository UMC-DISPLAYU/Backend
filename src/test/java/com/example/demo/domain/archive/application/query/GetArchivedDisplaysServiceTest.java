package com.example.demo.domain.archive.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.result.ArchiveDisplayCursorResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.archive.domain.type.ArchiveDisplayStatus;
import com.example.demo.domain.display.application.result.DisplaySummaryResult;
import com.example.demo.domain.display.application.usecase.GetDisplaySummariesUseCase;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class GetArchivedDisplaysServiceTest {

  private final ArchiveDisplayRepository archiveDisplayRepository =
      mock(ArchiveDisplayRepository.class);
  private final MemoRepository memoRepository = mock(MemoRepository.class);
  private final GetDisplaySummariesUseCase getDisplaySummariesUseCase =
      mock(GetDisplaySummariesUseCase.class);
  private final GetArchivedDisplaysService service =
      new GetArchivedDisplaysService(
          archiveDisplayRepository, memoRepository, getDisplaySummariesUseCase);

  @Test
  void getArchivedDisplaysReturnsDisplaySummaryAndArchiveValues() {
    ArchiveDisplay archiveDisplay =
        archiveDisplay(10L, 100L, 7L, LocalDateTime.of(2026, 7, 13, 1, 49, 28));
    Memo memo = Memo.createForDisplay("다시 볼 전시", null, 10L);
    DisplaySummaryResult summary =
        new DisplaySummaryResult(
            100L,
            "디자인 졸업전시",
            "디유대학교",
            "시각디자인과",
            "디유갤러리",
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(10),
            "https://cdn.displayu.com/posters/main.png");
    when(archiveDisplayRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, 21))
        .thenReturn(List.of(archiveDisplay));
    when(memoRepository.findByArchiveDisplayIdInAndDeletedAtIsNull(List.of(10L)))
        .thenReturn(List.of(memo));
    when(getDisplaySummariesUseCase.getDisplaySummaries(List.of(100L)))
        .thenReturn(List.of(summary));

    ArchiveDisplayCursorResult result = service.getArchivedDisplays(7L, null, 20);

    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursorId()).isNull();
    assertThat(result.displays()).hasSize(1);
    assertThat(result.displays().getFirst().archiveDisplayId()).isEqualTo(10L);
    assertThat(result.displays().getFirst().displayId()).isEqualTo(100L);
    assertThat(result.displays().getFirst().userId()).isEqualTo(7L);
    assertThat(result.displays().getFirst().memo()).isEqualTo("다시 볼 전시");
    assertThat(result.displays().getFirst().title()).isEqualTo("디자인 졸업전시");
    assertThat(result.displays().getFirst().posterImageUrl())
        .isEqualTo("https://cdn.displayu.com/posters/main.png");
    assertThat(result.displays().getFirst().status()).isEqualTo(ArchiveDisplayStatus.ONGOING);
  }

  @Test
  void getArchivedDisplaysKeepsArchiveValuesWhenSummaryIsMissing() {
    ArchiveDisplay archiveDisplay =
        archiveDisplay(10L, 100L, 7L, LocalDateTime.of(2026, 7, 13, 1, 49, 28));
    when(archiveDisplayRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, 21))
        .thenReturn(List.of(archiveDisplay));
    when(memoRepository.findByArchiveDisplayIdInAndDeletedAtIsNull(List.of(10L)))
        .thenReturn(List.of());
    when(getDisplaySummariesUseCase.getDisplaySummaries(List.of(100L))).thenReturn(List.of());

    ArchiveDisplayCursorResult result = service.getArchivedDisplays(7L, null, 20);

    assertThat(result.displays()).hasSize(1);
    assertThat(result.displays().getFirst().archiveDisplayId()).isEqualTo(10L);
    assertThat(result.displays().getFirst().displayId()).isEqualTo(100L);
    assertThat(result.displays().getFirst().title()).isNull();
    assertThat(result.displays().getFirst().posterImageUrl()).isNull();
    assertThat(result.displays().getFirst().status()).isNull();
  }

  @Test
  void getArchivedDisplaysReturnsEmptyListWhenNoArchivedDisplays() {
    when(archiveDisplayRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, 21))
        .thenReturn(List.of());

    ArchiveDisplayCursorResult result = service.getArchivedDisplays(7L, null, 20);

    assertThat(result.displays()).isEmpty();
    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursorId()).isNull();
  }

  @Test
  void getArchivedDisplaysReturnsNullPosterWhenSummaryHasNoPoster() {
    ArchiveDisplay archiveDisplay =
        archiveDisplay(10L, 100L, 7L, LocalDateTime.of(2026, 7, 13, 1, 49, 28));
    DisplaySummaryResult summary =
        new DisplaySummaryResult(
            100L,
            "디자인 졸업전시",
            "디유대학교",
            "시각디자인과",
            "디유갤러리",
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(10),
            null);
    when(archiveDisplayRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, 21))
        .thenReturn(List.of(archiveDisplay));
    when(memoRepository.findByArchiveDisplayIdInAndDeletedAtIsNull(List.of(10L)))
        .thenReturn(List.of());
    when(getDisplaySummariesUseCase.getDisplaySummaries(List.of(100L)))
        .thenReturn(List.of(summary));

    ArchiveDisplayCursorResult result = service.getArchivedDisplays(7L, null, 20);

    assertThat(result.displays().getFirst().posterImageUrl()).isNull();
    assertThat(result.displays().getFirst().title()).isEqualTo("디자인 졸업전시");
    assertThat(result.displays().getFirst().status()).isEqualTo(ArchiveDisplayStatus.ONGOING);
  }

  @Test
  void getArchivedDisplaysMapsEachSummaryToItsOwnDisplayId() {
    ArchiveDisplay first = archiveDisplay(10L, 100L, 7L, LocalDateTime.of(2026, 7, 13, 1, 49, 28));
    ArchiveDisplay second = archiveDisplay(11L, 200L, 7L, LocalDateTime.of(2026, 7, 12, 15, 10, 2));
    DisplaySummaryResult summaryForFirst =
        new DisplaySummaryResult(
            100L,
            "디자인 졸업전시",
            "디유대학교",
            "시각디자인과",
            "디유갤러리",
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(10),
            "https://cdn.displayu.com/posters/100.png");
    DisplaySummaryResult summaryForSecond =
        new DisplaySummaryResult(
            200L,
            "회화 전시",
            "다른대학교",
            "회화과",
            "다른갤러리",
            LocalDate.now().minusDays(30),
            LocalDate.now().minusDays(20),
            "https://cdn.displayu.com/posters/200.png");
    when(archiveDisplayRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, 21))
        .thenReturn(List.of(first, second));
    when(memoRepository.findByArchiveDisplayIdInAndDeletedAtIsNull(List.of(10L, 11L)))
        .thenReturn(List.of());
    when(getDisplaySummariesUseCase.getDisplaySummaries(List.of(100L, 200L)))
        .thenReturn(List.of(summaryForFirst, summaryForSecond));

    ArchiveDisplayCursorResult result = service.getArchivedDisplays(7L, null, 20);

    assertThat(result.displays()).hasSize(2);
    assertThat(result.displays().get(0).displayId()).isEqualTo(100L);
    assertThat(result.displays().get(0).title()).isEqualTo("디자인 졸업전시");
    assertThat(result.displays().get(0).status()).isEqualTo(ArchiveDisplayStatus.ONGOING);
    assertThat(result.displays().get(1).displayId()).isEqualTo(200L);
    assertThat(result.displays().get(1).title()).isEqualTo("회화 전시");
    assertThat(result.displays().get(1).status()).isEqualTo(ArchiveDisplayStatus.ENDED);
  }

  private static ArchiveDisplay archiveDisplay(
      Long archiveDisplayId, Long displayId, Long userId, LocalDateTime savedAt) {
    ArchiveDisplay archiveDisplay = ArchiveDisplay.create(displayId, userId);
    ReflectionTestUtils.setField(archiveDisplay, "id", archiveDisplayId);
    ReflectionTestUtils.setField(archiveDisplay, "savedAt", savedAt);
    return archiveDisplay;
  }
}
