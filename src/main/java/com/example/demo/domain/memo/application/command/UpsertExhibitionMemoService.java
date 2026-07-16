package com.example.demo.domain.memo.application.command;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.memo.application.result.MemoResult;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.error.MemoErrorCode;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Objects;
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
            .findById(command.archiveDisplayId())
            .filter(display -> display.getUserId().equals(command.userId()))
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

    Memo savedMemo = memoRepository.save(memo);
    return toResult(savedMemo);
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
