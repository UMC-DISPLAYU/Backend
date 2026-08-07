package com.example.demo.domain.archive.application.query;

import com.example.demo.domain.archive.application.result.ArchiveWorkCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveWorkResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.displayartwork.application.result.ArtworkSummaryResult;
import com.example.demo.domain.displayartwork.application.usecase.GetArtworkSummariesUseCase;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    List<ArchiveWork> fetched =
        archiveWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            userId, cursorId, pageSize + 1);

    boolean hasNext = fetched.size() > pageSize;
    List<ArchiveWork> works = hasNext ? fetched.subList(0, pageSize) : fetched;

    Map<Long, String> memoByArchiveWorkId =
        works.isEmpty()
            ? Map.of()
            : memoRepository
                .findByArchiveWorkIdInAndDeletedAtIsNull(
                    works.stream().map(ArchiveWork::getId).toList())
                .stream()
                .collect(Collectors.toMap(Memo::getArchiveWorkId, Memo::getContent));

    Map<Long, ArtworkSummaryResult> summaryByArtworkId =
        works.isEmpty()
            ? Map.of()
            : getArtworkSummariesUseCase
                .getArtworkSummaries(works.stream().map(ArchiveWork::getDisplayArtworkId).toList())
                .stream()
                .collect(
                    Collectors.toMap(ArtworkSummaryResult::displayArtworkId, summary -> summary));

    List<ArchiveWorkResult> results =
        works.stream()
            .map(
                archiveWork ->
                    ArchiveWorkResult.from(
                        archiveWork,
                        memoByArchiveWorkId.get(archiveWork.getId()),
                        summaryByArtworkId.get(archiveWork.getDisplayArtworkId())))
            .toList();

    Long nextCursorId = hasNext ? works.get(works.size() - 1).getId() : null;
    return new ArchiveWorkCursorResult(results, nextCursorId, pageSize, hasNext);
  }
}
