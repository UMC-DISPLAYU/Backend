package com.example.demo.domain.memo.application.command;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.memo.application.result.MemoResult;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.error.MemoErrorCode;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpsertExhibitionMemoService {

  private final ArchiveDisplayRepository archiveDisplayRepository;
  private final MemoRepository memoRepository;

  public UpsertExhibitionMemoService(
      ArchiveDisplayRepository archiveDisplayRepository, MemoRepository memoRepository) {
    this.archiveDisplayRepository = archiveDisplayRepository;
    this.memoRepository = memoRepository;
  }

  @Transactional
  public MemoResult upsertExhibitionMemo(UpsertExhibitionMemoCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    ArchiveDisplay archiveDisplay =
        archiveDisplayRepository
            .findByIdAndUserId(command.archiveDisplayId(), command.userId())
            .orElseThrow(() -> new BusinessException(MemoErrorCode.ARCHIVE_DISPLAY_NOT_FOUND));

    Memo memo =
        memoRepository
            .findByArchiveDisplayIdAndDeletedAtIsNull(archiveDisplay.getId())
            .map(
                existing -> {
                  existing.changeContent(command.content(), command.visitDate());
                  return existing;
                })
            .orElseGet(
                () ->
                    Memo.createForDisplay(
                        command.content(), command.visitDate(), archiveDisplay.getId()));

    Memo savedMemo;
    try {
      savedMemo = memoRepository.save(memo);
    } catch (DataIntegrityViolationException e) {
      // 동시 요청으로 두 요청 모두 "기존 메모 없음"으로 판단해 동시에 새로 생성하려는 경우,
      // DB의 유니크 제약(UQ_MEMO_ACTIVE_ARCHIVEDISPLAY)이 최종 방어선 역할을 함.
      if (isActiveArchiveDisplayUniqueConstraintViolation(e)) {
        throw new BusinessException(MemoErrorCode.MEMO_CONCURRENT_WRITE_CONFLICT, e);
      }
      throw e;
    }
    return toResult(savedMemo);
  }

  private boolean isActiveArchiveDisplayUniqueConstraintViolation(
      DataIntegrityViolationException e) {
    String message = e.getMostSpecificCause().getMessage();
    return message != null && message.contains("UQ_MEMO_ACTIVE_ARCHIVEDISPLAY");
  }

  private MemoResult toResult(Memo memo) {
    return new MemoResult(
        memo.getId(),
        memo.getArchiveDisplayId(),
        memo.getArchiveWorkId(),
        memo.getContent(),
        memo.getVisitDate(),
        memo.getCreatedAt(),
        memo.getUpdatedAt());
  }
}
