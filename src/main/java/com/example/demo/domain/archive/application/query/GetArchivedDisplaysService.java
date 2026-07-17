package com.example.demo.domain.archive.application.query;

import com.example.demo.domain.archive.application.result.ArchiveDisplayCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveDisplayResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetArchivedDisplaysService {

  private static final int MAX_PAGE_SIZE = 50;

  private final ArchiveDisplayRepository archiveDisplayRepository;
  private final MemoRepository memoRepository;

  public GetArchivedDisplaysService(
      ArchiveDisplayRepository archiveDisplayRepository, MemoRepository memoRepository) {
    this.archiveDisplayRepository = archiveDisplayRepository;
    this.memoRepository = memoRepository;
  }

  @Transactional(readOnly = true)
  public ArchiveDisplayCursorResult getArchivedDisplays(Long userId, Long cursorId, int size) {
    int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    List<ArchiveDisplay> fetched =
        archiveDisplayRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            userId, cursorId, pageSize + 1);

    boolean hasNext = fetched.size() > pageSize;
    List<ArchiveDisplay> displays = hasNext ? fetched.subList(0, pageSize) : fetched;

    Map<Long, String> memoByArchiveDisplayId =
        memoRepository
            .findByArchiveDisplayIdInAndDeletedAtIsNull(
                displays.stream().map(ArchiveDisplay::getId).toList())
            .stream()
            .collect(Collectors.toMap(Memo::getArchiveDisplayId, Memo::getContent));

    List<ArchiveDisplayResult> results =
        displays.stream()
            .map(
                archiveDisplay ->
                    ArchiveDisplayResult.from(
                        archiveDisplay, memoByArchiveDisplayId.get(archiveDisplay.getId())))
            .toList();

    Long nextCursorId = hasNext ? displays.get(displays.size() - 1).getId() : null;
    return new ArchiveDisplayCursorResult(results, nextCursorId, pageSize, hasNext);
  }
}
