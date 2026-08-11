package com.example.demo.domain.archive.application.query;

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 저장된 작품 목록은 전시 작품(ArchiveWork)과 개인 작품(ArchivePersonalWork) 두 소스를 savedAt 기준으로 병합해 하나의 커서 목록으로
 * 내려준다. 두 테이블의 저장 기록 ID가 서로 겹칠 수 있어, 커서 값은 signedId(전시 작품=+id, 개인 작품=-id)로 인코딩한다. 프론트는 이 값을 그대로
 * 돌려주기만 하면 되는 완전한 opaque 커서다.
 */
@Service
public class GetArchivedWorksService {

  private static final int MAX_PAGE_SIZE = 50;

  private final ArchiveWorkRepository archiveWorkRepository;
  private final ArchivePersonalWorkRepository archivePersonalWorkRepository;
  private final MemoRepository memoRepository;
  private final GetArtworkSummariesUseCase getArtworkSummariesUseCase;
  private final GetPersonalArtworkSummariesUseCase getPersonalArtworkSummariesUseCase;

  public GetArchivedWorksService(
      ArchiveWorkRepository archiveWorkRepository,
      ArchivePersonalWorkRepository archivePersonalWorkRepository,
      MemoRepository memoRepository,
      GetArtworkSummariesUseCase getArtworkSummariesUseCase,
      GetPersonalArtworkSummariesUseCase getPersonalArtworkSummariesUseCase) {
    this.archiveWorkRepository = archiveWorkRepository;
    this.archivePersonalWorkRepository = archivePersonalWorkRepository;
    this.memoRepository = memoRepository;
    this.getArtworkSummariesUseCase = getArtworkSummariesUseCase;
    this.getPersonalArtworkSummariesUseCase = getPersonalArtworkSummariesUseCase;
  }

  @Transactional(readOnly = true)
  public ArchiveWorkCursorResult getArchivedWorks(Long userId, Long cursorId, int size) {
    int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

    // cursorId가 존재하는데 어느 테이블에서도 찾지 못하면(위조/만료된 커서) 이후를 조회할 기준점이 없으므로 빈 페이지를 반환한다.
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
    List<ArchivePersonalWork> personalWorks =
        archivePersonalWorkRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            userId, cursorSavedAt, cursorId, pageSize + 1);

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

    Map<Long, PersonalArtworkSummaryResult> summaryByPersonalArtworkId =
        personalWorks.isEmpty()
            ? Map.of()
            : getPersonalArtworkSummariesUseCase
                .getPersonalArtworkSummaries(
                    personalWorks.stream().map(ArchivePersonalWork::getPersonalArtworkId).toList())
                .stream()
                .collect(
                    Collectors.toMap(
                        PersonalArtworkSummaryResult::personalArtworkId, summary -> summary));

    List<SignedResult> merged = new ArrayList<>(works.size() + personalWorks.size());
    for (ArchiveWork work : works) {
      merged.add(
          new SignedResult(
              work.getSavedAt(),
              work.getId(),
              ArchiveWorkResult.from(
                  work,
                  memoByArchiveWorkId.get(work.getId()),
                  summaryByArtworkId.get(work.getDisplayArtworkId()))));
    }
    for (ArchivePersonalWork personalWork : personalWorks) {
      merged.add(
          new SignedResult(
              personalWork.getSavedAt(),
              -personalWork.getId(),
              ArchiveWorkResult.fromPersonal(
                  personalWork,
                  summaryByPersonalArtworkId.get(personalWork.getPersonalArtworkId()))));
    }
    // savedAt DESC, signedId DESC — signedId가 클수록(전시 작품이 항상 개인 작품보다 우선) 먼저 온다.
    merged.sort(
        Comparator.<SignedResult, LocalDateTime>comparing(r -> r.savedAt, Comparator.reverseOrder())
            .thenComparing(r -> r.signedId, Comparator.reverseOrder()));

    boolean hasNext = merged.size() > pageSize;
    List<SignedResult> page = hasNext ? merged.subList(0, pageSize) : merged;

    Long nextCursorId = hasNext ? page.get(page.size() - 1).signedId : null;
    List<ArchiveWorkResult> results = page.stream().map(r -> r.result).toList();
    return new ArchiveWorkCursorResult(results, nextCursorId, pageSize, hasNext);
  }

  // cursorId는 signedId(전시 작품=+id, 개인 작품=-id)다. 부호로 어느 테이블에서 조회할지 결정한다.
  private LocalDateTime resolveCursorSavedAt(Long userId, Long cursorId) {
    if (cursorId == null) {
      return null;
    }
    if (cursorId > 0) {
      return archiveWorkRepository
          .findByIdAndUserId(cursorId, userId)
          .map(ArchiveWork::getSavedAt)
          .orElse(null);
    }
    return archivePersonalWorkRepository
        .findByIdAndUserId(-cursorId, userId)
        .map(ArchivePersonalWork::getSavedAt)
        .orElse(null);
  }

  private record SignedResult(LocalDateTime savedAt, Long signedId, ArchiveWorkResult result) {}
}
