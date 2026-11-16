package com.example.demo.domain.archive.application.query;

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
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 저장된 작품 목록은 전시 작품(ArchiveWork) 저장 기록만 savedAt 기준 커서 목록으로 내려준다. 개인 작품 기능이 서비스에서 제외되어 개인 작품 저장
 * 기록(ArchivePersonalWork)은 데이터만 보존하고 목록에는 포함하지 않는다. 커서 값은 전시 작품 저장 기록 ID(양수)다.
 */
@Service
public class GetArchivedWorksService {

  private static final int MAX_PAGE_SIZE = 50;

  private final ArchiveWorkRepository archiveWorkRepository;
  private final MemoRepository memoRepository;
  private final GetArtworkSummariesUseCase getArtworkSummariesUseCase;

  public GetArchivedWorksService(
      ArchiveWorkRepository archiveWorkRepository,
      MemoRepository memoRepository,
      GetArtworkSummariesUseCase getArtworkSummariesUseCase) {
    this.archiveWorkRepository = archiveWorkRepository;
    this.memoRepository = memoRepository;
    this.getArtworkSummariesUseCase = getArtworkSummariesUseCase;
  }

  @Transactional(readOnly = true)
  public ArchiveWorkCursorResult getArchivedWorks(Long userId, Long cursorId, int size) {
    int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

    // cursorId가 존재하는데 찾지 못하면(위조/만료된 커서, 이전 개인 작품 커서인 음수 값) 이후를 조회할 기준점이 없으므로 빈 페이지를 반환한다.
    LocalDateTime cursorSavedAt;
    if (cursorId == null) {
      cursorSavedAt = null;
    } else {
      cursorSavedAt = resolveCursorSavedAt(userId, cursorId);
      if (cursorSavedAt == null) {
        return new ArchiveWorkCursorResult(List.of(), null, pageSize, false);
      }
    }

    List<ArchiveWork> works =
        archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            userId, cursorSavedAt, cursorId, pageSize + 1);

    boolean hasNext = works.size() > pageSize;
    List<ArchiveWork> page = hasNext ? works.subList(0, pageSize) : works;
    if (page.isEmpty()) {
      return new ArchiveWorkCursorResult(List.of(), null, pageSize, false);
    }

    Map<Long, String> memoByArchiveWorkId =
        memoRepository
            .findByArchiveWorkIdInAndDeletedAtIsNull(page.stream().map(ArchiveWork::getId).toList())
            .stream()
            .collect(Collectors.toMap(Memo::getArchiveWorkId, Memo::getContent));

    Map<Long, ArtworkSummaryResult> summaryByArtworkId =
        getArtworkSummariesUseCase
            .getArtworkSummaries(page.stream().map(ArchiveWork::getDisplayArtworkId).toList())
            .stream()
            .collect(Collectors.toMap(ArtworkSummaryResult::displayArtworkId, summary -> summary));

    List<ArchiveWorkResult> results =
        page.stream()
            .map(
                work ->
                    ArchiveWorkResult.from(
                        work,
                        memoByArchiveWorkId.get(work.getId()),
                        summaryByArtworkId.get(work.getDisplayArtworkId())))
            .toList();
    Long nextCursorId = hasNext ? page.get(page.size() - 1).getId() : null;
    return new ArchiveWorkCursorResult(results, nextCursorId, pageSize, hasNext);
  }

  private LocalDateTime resolveCursorSavedAt(Long userId, Long cursorId) {
    if (cursorId <= 0) {
      return null;
    }
    return archiveWorkRepository
        .findByIdAndUserId(cursorId, userId)
        .map(ArchiveWork::getSavedAt)
        .orElse(null);
  }
}
