package com.example.demo.domain.memo.application.command;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.memo.application.permission.MemoPermissionChecker;
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
public class UpsertArtworkMemoService {

  private final ArchiveWorkRepository archiveWorkRepository;
  private final MemoRepository memoRepository;
  private final MemoPermissionChecker memoPermissionChecker;

  public UpsertArtworkMemoService(
      ArchiveWorkRepository archiveWorkRepository,
      MemoRepository memoRepository,
      MemoPermissionChecker memoPermissionChecker) {
    this.archiveWorkRepository = archiveWorkRepository;
    this.memoRepository = memoRepository;
    this.memoPermissionChecker = memoPermissionChecker;
  }

  @Transactional
  public MemoResult upsertArtworkMemo(UpsertArtworkMemoCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    ArchiveWork archiveWork =
        archiveWorkRepository
            .findById(command.archiveWorkId())
            .orElseThrow(() -> new BusinessException(MemoErrorCode.ARCHIVE_WORK_NOT_FOUND));
    memoPermissionChecker.requireArchiveOwner(archiveWork, command.userId());

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

    Memo savedMemo;
    try {
      savedMemo = memoRepository.save(memo);
    } catch (DataIntegrityViolationException e) {
      // 동시 요청으로 두 요청 모두 "기존 메모 없음"으로 판단해 동시에 새로 생성하려는 경우,
      // DB의 유니크 제약(UQ_MEMO_ACTIVE_ARCHIVEWORK)이 최종 방어선 역할을 함.
      if (isActiveArchiveWorkUniqueConstraintViolation(e)) {
        throw new BusinessException(MemoErrorCode.MEMO_CONCURRENT_WRITE_CONFLICT, e);
      }
      throw e;
    }
    return toResult(savedMemo);
  }

  private boolean isActiveArchiveWorkUniqueConstraintViolation(DataIntegrityViolationException e) {
    String message = e.getMostSpecificCause().getMessage();
    return message != null && message.contains("UQ_MEMO_ACTIVE_ARCHIVEWORK");
  }

  private MemoResult toResult(Memo memo) {
    return new MemoResult(
        memo.getId(),
        memo.getArchiveDisplayId(),
        memo.getArchiveWorkId(),
        memo.getArchivePersonalWorkId(),
        memo.getContent(),
        memo.getVisitDate(),
        memo.getCreatedAt(),
        memo.getUpdatedAt());
  }
}
