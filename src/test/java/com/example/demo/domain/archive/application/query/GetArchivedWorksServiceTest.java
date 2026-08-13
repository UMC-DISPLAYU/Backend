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
import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.repository.ArchivePersonalWorkRepository;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.displayartwork.application.result.ArtworkSummaryResult;
import com.example.demo.domain.displayartwork.application.usecase.GetArtworkSummariesUseCase;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import com.example.demo.domain.personalartwork.application.usecase.GetPersonalArtworkSummariesUseCase;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class GetArchivedWorksServiceTest {

  private final ArchiveWorkRepository archiveWorkRepository = mock(ArchiveWorkRepository.class);
  private final ArchivePersonalWorkRepository archivePersonalWorkRepository =
      mock(ArchivePersonalWorkRepository.class);
  private final MemoRepository memoRepository = mock(MemoRepository.class);
  private final GetArtworkSummariesUseCase getArtworkSummariesUseCase =
      mock(GetArtworkSummariesUseCase.class);
  private final GetPersonalArtworkSummariesUseCase getPersonalArtworkSummariesUseCase =
      mock(GetPersonalArtworkSummariesUseCase.class);
  private final GetArchivedWorksService service =
      new GetArchivedWorksService(
          archiveWorkRepository,
          archivePersonalWorkRepository,
          memoRepository,
          getArtworkSummariesUseCase,
          getPersonalArtworkSummariesUseCase);

  @Test
  void mergesDisplayAndPersonalWorksBySavedAtDescWithFullFieldMapping() {
    ArchiveWork work = archiveWork(10L, 100L, 7L, LocalDateTime.of(2026, 8, 11, 10, 0, 0));
    ArchivePersonalWork personalWork =
        archivePersonalWork(20L, 300L, 7L, LocalDateTime.of(2026, 8, 11, 9, 0, 0));
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, null, 21))
        .thenReturn(List.of(work));
    when(archivePersonalWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, null, null, 21))
        .thenReturn(List.of(personalWork));
    when(memoRepository.findByArchiveWorkIdInAndDeletedAtIsNull(List.of(10L)))
        .thenReturn(List.of());
    when(memoRepository.findByArchivePersonalWorkIdInAndDeletedAtIsNull(List.of(20L)))
        .thenReturn(List.of());
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(100L)))
        .thenReturn(
            List.of(new ArtworkSummaryResult(100L, "FORM 2026", "고상준", "https://cdn/1.png")));
    when(getPersonalArtworkSummariesUseCase.getPersonalArtworkSummaries(List.of(300L)))
        .thenReturn(
            List.of(
                new PersonalArtworkSummaryResult(
                    300L, "작은 정원", "https://cdn/garden.png", "COMPLEX", LocalDateTime.now())));

    ArchiveWorkCursorResult result = service.getArchivedWorks(7L, null, 20);

    assertThat(result.works()).hasSize(2);
    ArchiveWorkResult first = result.works().get(0);
    assertThat(first.archiveWorkId()).isEqualTo(10L);
    assertThat(first.displayArtworkId()).isEqualTo(100L);
    assertThat(first.personalArtworkId()).isNull();
    assertThat(first.artworkName()).isEqualTo("FORM 2026");
    assertThat(first.artistName()).isEqualTo("고상준");

    ArchiveWorkResult second = result.works().get(1);
    assertThat(second.archiveWorkId()).isEqualTo(20L);
    assertThat(second.displayArtworkId()).isNull();
    assertThat(second.personalArtworkId()).isEqualTo(300L);
    assertThat(second.artworkName()).isEqualTo("작은 정원");
    assertThat(second.artworkImageUrl()).isEqualTo("https://cdn/garden.png");
    assertThat(second.artistName()).isNull();
    assertThat(second.memo()).isNull();

    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursorId()).isNull();
  }

  @Test
  void includesPersonalWorkMemoWhenOneExists() {
    ArchivePersonalWork personalWork =
        archivePersonalWork(20L, 300L, 7L, LocalDateTime.of(2026, 8, 11, 9, 0, 0));
    Memo memo = Memo.createForPersonalWork("제일 아끼는 작품", null, 20L);
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, null, 21))
        .thenReturn(List.of());
    when(archivePersonalWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, null, null, 21))
        .thenReturn(List.of(personalWork));
    when(memoRepository.findByArchivePersonalWorkIdInAndDeletedAtIsNull(List.of(20L)))
        .thenReturn(List.of(memo));
    when(getPersonalArtworkSummariesUseCase.getPersonalArtworkSummaries(List.of(300L)))
        .thenReturn(List.of());

    ArchiveWorkCursorResult result = service.getArchivedWorks(7L, null, 20);

    assertThat(result.works()).hasSize(1);
    assertThat(result.works().get(0).memo()).isEqualTo("제일 아끼는 작품");
  }

  @Test
  void tieBreaksBySignedIdWhenSavedAtIsEqual() {
    LocalDateTime sameSavedAt = LocalDateTime.of(2026, 8, 11, 10, 0, 0);
    ArchiveWork work = archiveWork(10L, 100L, 7L, sameSavedAt);
    ArchivePersonalWork personalWork = archivePersonalWork(10L, 300L, 7L, sameSavedAt);
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, null, 21))
        .thenReturn(List.of(work));
    when(archivePersonalWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, null, null, 21))
        .thenReturn(List.of(personalWork));
    when(memoRepository.findByArchiveWorkIdInAndDeletedAtIsNull(List.of(10L)))
        .thenReturn(List.of());
    when(memoRepository.findByArchivePersonalWorkIdInAndDeletedAtIsNull(List.of(10L)))
        .thenReturn(List.of());
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(100L))).thenReturn(List.of());
    when(getPersonalArtworkSummariesUseCase.getPersonalArtworkSummaries(List.of(300L)))
        .thenReturn(List.of());

    ArchiveWorkCursorResult result = service.getArchivedWorks(7L, null, 20);

    // 같은 savedAt이면 signedId가 큰 쪽(전시 작품, +id)이 항상 먼저 온다.
    assertThat(result.works()).hasSize(2);
    assertThat(result.works().get(0).displayArtworkId()).isEqualTo(100L);
    assertThat(result.works().get(1).personalArtworkId()).isEqualTo(300L);
  }

  @Test
  void returnsEmptyWithoutQueryingEnrichmentWhenNoArchivedItems() {
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, null, 21))
        .thenReturn(List.of());
    when(archivePersonalWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, null, null, 21))
        .thenReturn(List.of());

    ArchiveWorkCursorResult result = service.getArchivedWorks(7L, null, 20);

    assertThat(result.works()).isEmpty();
    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursorId()).isNull();
    verifyNoInteractions(
        memoRepository, getArtworkSummariesUseCase, getPersonalArtworkSummariesUseCase);
  }

  @Test
  void resolvesCursorFromArchiveWorkWhenCursorIdIsPositive() {
    LocalDateTime cursorSavedAt = LocalDateTime.of(2026, 8, 11, 8, 0, 0);
    ArchiveWork cursorWork = archiveWork(5L, 999L, 7L, cursorSavedAt);
    when(archiveWorkRepository.findByIdAndUserId(5L, 7L)).thenReturn(Optional.of(cursorWork));
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, cursorSavedAt, 5L, 21))
        .thenReturn(List.of());
    when(archivePersonalWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, cursorSavedAt, 5L, 21))
        .thenReturn(List.of());

    service.getArchivedWorks(7L, 5L, 20);

    verify(archiveWorkRepository)
        .findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, cursorSavedAt, 5L, 21);
    verify(archivePersonalWorkRepository)
        .findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, cursorSavedAt, 5L, 21);
    verify(archivePersonalWorkRepository, never()).findByIdAndUserId(anyLong(), anyLong());
  }

  @Test
  void resolvesCursorFromArchivePersonalWorkWhenCursorIdIsNegative() {
    LocalDateTime cursorSavedAt = LocalDateTime.of(2026, 8, 11, 8, 0, 0);
    ArchivePersonalWork cursorPersonalWork = archivePersonalWork(3L, 999L, 7L, cursorSavedAt);
    when(archivePersonalWorkRepository.findByIdAndUserId(3L, 7L))
        .thenReturn(Optional.of(cursorPersonalWork));
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, cursorSavedAt, -3L, 21))
        .thenReturn(List.of());
    when(archivePersonalWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, cursorSavedAt, -3L, 21))
        .thenReturn(List.of());

    service.getArchivedWorks(7L, -3L, 20);

    verify(archiveWorkRepository)
        .findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, cursorSavedAt, -3L, 21);
    verify(archivePersonalWorkRepository)
        .findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, cursorSavedAt, -3L, 21);
    verify(archiveWorkRepository, never()).findByIdAndUserId(anyLong(), anyLong());
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
    verify(archivePersonalWorkRepository, never())
        .findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(eq(7L), any(), any(), anyInt());
  }

  @Test
  void computesPositiveNextCursorIdWhenLastPageItemIsDisplayWork() {
    ArchiveWork newer = archiveWork(20L, 100L, 7L, LocalDateTime.of(2026, 8, 11, 10, 0, 0));
    ArchiveWork older = archiveWork(10L, 200L, 7L, LocalDateTime.of(2026, 8, 11, 9, 0, 0));
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, null, 2))
        .thenReturn(List.of(newer, older));
    when(archivePersonalWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, null, null, 2))
        .thenReturn(List.of());
    when(memoRepository.findByArchiveWorkIdInAndDeletedAtIsNull(List.of(20L)))
        .thenReturn(List.of());
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(100L))).thenReturn(List.of());

    ArchiveWorkCursorResult result = service.getArchivedWorks(7L, null, 1);

    assertThat(result.works()).hasSize(1);
    assertThat(result.hasNext()).isTrue();
    assertThat(result.nextCursorId()).isEqualTo(20L);
  }

  @Test
  void computesNegativeNextCursorIdWhenLastPageItemIsPersonalWork() {
    ArchiveWork work = archiveWork(30L, 100L, 7L, LocalDateTime.of(2026, 8, 11, 11, 0, 0));
    ArchivePersonalWork newerPersonal =
        archivePersonalWork(15L, 300L, 7L, LocalDateTime.of(2026, 8, 11, 10, 0, 0));
    ArchivePersonalWork olderPersonal =
        archivePersonalWork(5L, 400L, 7L, LocalDateTime.of(2026, 8, 11, 9, 0, 0));
    when(archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(7L, null, null, 3))
        .thenReturn(List.of(work));
    when(archivePersonalWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, null, null, 3))
        .thenReturn(List.of(newerPersonal, olderPersonal));
    when(memoRepository.findByArchiveWorkIdInAndDeletedAtIsNull(List.of(30L)))
        .thenReturn(List.of());
    when(memoRepository.findByArchivePersonalWorkIdInAndDeletedAtIsNull(List.of(15L, 5L)))
        .thenReturn(List.of());
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(100L))).thenReturn(List.of());
    when(getPersonalArtworkSummariesUseCase.getPersonalArtworkSummaries(List.of(300L, 400L)))
        .thenReturn(List.of());

    ArchiveWorkCursorResult result = service.getArchivedWorks(7L, null, 2);

    // savedAt DESC: work(11:00) -> newerPersonal(10:00) -> olderPersonal(9:00). pageSize=2이므로
    // [work, newerPersonal]까지가 이번 페이지, 마지막 항목은 개인 작품이라 커서가 음수여야 한다.
    assertThat(result.works()).hasSize(2);
    assertThat(result.hasNext()).isTrue();
    assertThat(result.nextCursorId()).isEqualTo(-15L);
  }

  private static ArchiveWork archiveWork(
      Long archiveWorkId, Long displayArtworkId, Long userId, LocalDateTime savedAt) {
    ArchiveWork archiveWork = ArchiveWork.create(displayArtworkId, userId);
    ReflectionTestUtils.setField(archiveWork, "id", archiveWorkId);
    ReflectionTestUtils.setField(archiveWork, "savedAt", savedAt);
    return archiveWork;
  }

  private static ArchivePersonalWork archivePersonalWork(
      Long archivePersonalWorkId, Long personalArtworkId, Long userId, LocalDateTime savedAt) {
    ArchivePersonalWork archivePersonalWork = ArchivePersonalWork.create(personalArtworkId, userId);
    ReflectionTestUtils.setField(archivePersonalWork, "id", archivePersonalWorkId);
    ReflectionTestUtils.setField(archivePersonalWork, "savedAt", savedAt);
    return archivePersonalWork;
  }
}
