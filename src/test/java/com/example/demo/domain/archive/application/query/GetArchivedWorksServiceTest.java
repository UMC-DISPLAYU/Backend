package com.example.demo.domain.archive.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.result.ArchiveWorkCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveWorkResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.displayartwork.application.result.ArtworkSummaryResult;
import com.example.demo.domain.displayartwork.application.usecase.GetArtworkSummariesUseCase;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class GetArchivedWorksServiceTest {

  private final ArchiveWorkRepository archiveWorkRepository = mock(ArchiveWorkRepository.class);
  private final MemoRepository memoRepository = mock(MemoRepository.class);
  private final GetArtworkSummariesUseCase getArtworkSummariesUseCase =
      mock(GetArtworkSummariesUseCase.class);
  private final GetArchivedWorksService service =
      new GetArchivedWorksService(
          archiveWorkRepository, memoRepository, getArtworkSummariesUseCase);

  @Test
  void returnsDisplayWorksBySavedAtDescWithFullFieldMapping() {
    ArchiveWork newer = archiveWork(10L, 100L, 7L, LocalDateTime.of(2026, 8, 11, 10, 0, 0));
    ArchiveWork older = archiveWork(8L, 200L, 7L, LocalDateTime.of(2026, 8, 11, 9, 0, 0));
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, null, 21))
        .thenReturn(List.of(newer, older));
    when(memoRepository.findByArchiveWorkIdInAndDeletedAtIsNull(List.of(10L, 8L)))
        .thenReturn(List.of());
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(100L, 200L)))
        .thenReturn(
            List.of(
                new ArtworkSummaryResult(100L, "FORM 2026", "고상준", "https://cdn/1.png"),
                new ArtworkSummaryResult(200L, "작은 정원", "김마야", "https://cdn/2.png")));

    ArchiveWorkCursorResult result = service.getArchivedWorks(7L, null, 20);

    assertThat(result.works()).hasSize(2);
    ArchiveWorkResult first = result.works().get(0);
    assertThat(first.archiveWorkId()).isEqualTo(10L);
    assertThat(first.displayArtworkId()).isEqualTo(100L);
    assertThat(first.personalArtworkId()).isNull();
    assertThat(first.artworkName()).isEqualTo("FORM 2026");
    assertThat(first.artistName()).isEqualTo("고상준");
    assertThat(first.artworkImageUrl()).isEqualTo("https://cdn/1.png");
    assertThat(first.memo()).isNull();
    assertThat(result.works().get(1).displayArtworkId()).isEqualTo(200L);

    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursorId()).isNull();
  }

  @Test
  void includesMemoWhenOneExists() {
    ArchiveWork work = archiveWork(10L, 100L, 7L, LocalDateTime.of(2026, 8, 11, 10, 0, 0));
    Memo memo = Memo.createForWork("이 작품의 색감이 좋았다.", null, 10L);
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, null, 21))
        .thenReturn(List.of(work));
    when(memoRepository.findByArchiveWorkIdInAndDeletedAtIsNull(List.of(10L)))
        .thenReturn(List.of(memo));
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(100L))).thenReturn(List.of());

    ArchiveWorkCursorResult result = service.getArchivedWorks(7L, null, 20);

    assertThat(result.works()).hasSize(1);
    assertThat(result.works().get(0).memo()).isEqualTo("이 작품의 색감이 좋았다.");
  }

  @Test
  void returnsEmptyWithoutQueryingEnrichmentWhenNoArchivedItems() {
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, null, 21))
        .thenReturn(List.of());

    ArchiveWorkCursorResult result = service.getArchivedWorks(7L, null, 20);

    assertThat(result.works()).isEmpty();
    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursorId()).isNull();
    verifyNoInteractions(memoRepository, getArtworkSummariesUseCase);
  }

  @Test
  void resolvesCursorFromArchiveWorkWhenCursorIdIsPositive() {
    LocalDateTime cursorSavedAt = LocalDateTime.of(2026, 8, 11, 8, 0, 0);
    ArchiveWork cursorWork = archiveWork(5L, 999L, 7L, cursorSavedAt);
    when(archiveWorkRepository.findByIdAndUserId(5L, 7L)).thenReturn(Optional.of(cursorWork));
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, cursorSavedAt, 5L, 21))
        .thenReturn(List.of());

    service.getArchivedWorks(7L, 5L, 20);

    verify(archiveWorkRepository)
        .findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, cursorSavedAt, 5L, 21);
  }

  @Test
  void returnsEmptyWhenCursorIsPreviousPersonalWorkCursor() {
    // 개인 작품 병합 시절의 커서는 음수였다. 더 이상 기준점을 찾을 수 없으므로 빈 페이지로 끝낸다.
    ArchiveWorkCursorResult result = service.getArchivedWorks(7L, -3L, 20);

    assertThat(result.works()).isEmpty();
    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursorId()).isNull();
    verify(archiveWorkRepository, never()).findByIdAndUserId(anyLong(), anyLong());
    verify(archiveWorkRepository, never())
        .findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(eq(7L), any(), any(), anyInt());
  }

  @Test
  void returnsEmptyAndSkipsQueriesWhenCursorIsInvalid() {
    when(archiveWorkRepository.findByIdAndUserId(999L, 7L)).thenReturn(Optional.empty());

    ArchiveWorkCursorResult result = service.getArchivedWorks(7L, 999L, 20);

    assertThat(result.works()).isEmpty();
    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursorId()).isNull();
    verify(archiveWorkRepository, never())
        .findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(eq(7L), any(), any(), anyInt());
  }

  @Test
  void computesNextCursorIdFromLastPageItemWhenMoreItemsExist() {
    ArchiveWork newer = archiveWork(20L, 100L, 7L, LocalDateTime.of(2026, 8, 11, 10, 0, 0));
    ArchiveWork older = archiveWork(10L, 200L, 7L, LocalDateTime.of(2026, 8, 11, 9, 0, 0));
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, null, 2))
        .thenReturn(List.of(newer, older));
    when(memoRepository.findByArchiveWorkIdInAndDeletedAtIsNull(List.of(20L)))
        .thenReturn(List.of());
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(100L))).thenReturn(List.of());

    ArchiveWorkCursorResult result = service.getArchivedWorks(7L, null, 1);

    assertThat(result.works()).hasSize(1);
    assertThat(result.hasNext()).isTrue();
    assertThat(result.nextCursorId()).isEqualTo(20L);
  }

  private static ArchiveWork archiveWork(
      Long archiveWorkId, Long displayArtworkId, Long userId, LocalDateTime savedAt) {
    ArchiveWork archiveWork = ArchiveWork.create(displayArtworkId, userId);
    ReflectionTestUtils.setField(archiveWork, "id", archiveWorkId);
    ReflectionTestUtils.setField(archiveWork, "savedAt", savedAt);
    return archiveWork;
  }
}
