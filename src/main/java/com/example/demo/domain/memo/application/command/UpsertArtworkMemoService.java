package com.example.demo.domain.memo.application.command;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.memo.application.result.MemoResult;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.error.MemoErrorCode;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpsertArtworkMemoService {

  private final ArchiveWorkRepository archiveWorkRepository;
  private final MemoRepository memoRepository;

  public UpsertArtworkMemoService(
      ArchiveWorkRepository archiveWorkRepository, MemoRepository memoRepository) {
    this.archiveWorkRepository = archiveWorkRepository;
    this.memoRepository = memoRepository;
  }

  @Transactional
  public MemoResult upsertArtworkMemo(UpsertArtworkMemoCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    ArchiveWork archiveWork =
        archiveWorkRepository
            .findByIdAndUserId(command.archiveWorkId(), command.userId())
            .orElseThrow(() -> new BusinessException(MemoErrorCode.ARCHIVE_WORK_NOT_FOUND));

    Memo memo =
        memoRepository
            .findByArchiveWorkIdAndDeletedAtIsNull(archiveWork.getId())
            .map(
                existing -> {
                  existing.changeContent(command.content(), command.visitDate());
                  return existing;
                })
            .orElseGet(
                () ->
                    Memo.createForWork(
                        command.content(), command.visitDate(), archiveWork.getId()));

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
