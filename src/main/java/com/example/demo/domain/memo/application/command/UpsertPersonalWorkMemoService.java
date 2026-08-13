package com.example.demo.domain.memo.application.command;

import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.repository.ArchivePersonalWorkRepository;
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
public class UpsertPersonalWorkMemoService {

  private final ArchivePersonalWorkRepository archivePersonalWorkRepository;
  private final MemoRepository memoRepository;
  private final MemoPermissionChecker memoPermissionChecker;

  public UpsertPersonalWorkMemoService(
      ArchivePersonalWorkRepository archivePersonalWorkRepository,
      MemoRepository memoRepository,
      MemoPermissionChecker memoPermissionChecker) {
    this.archivePersonalWorkRepository = archivePersonalWorkRepository;
    this.memoRepository = memoRepository;
    this.memoPermissionChecker = memoPermissionChecker;
  }

  @Transactional
  public MemoResult upsertPersonalWorkMemo(UpsertPersonalWorkMemoCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    ArchivePersonalWork archivePersonalWork =
        archivePersonalWorkRepository
            .findById(command.archivePersonalWorkId())
            .orElseThrow(
                () -> new BusinessException(MemoErrorCode.ARCHIVE_PERSONAL_WORK_NOT_FOUND));
    memoPermissionChecker.requireArchiveOwner(archivePersonalWork, command.userId());

    Memo memo =
        memoRepository
            .findByArchivePersonalWorkIdAndDeletedAtIsNull(archivePersonalWork.getId())
            .map(
                existing -> {
                  existing.changeContent(command.content(), command.visitDate());
                  return existing;
                })
            .orElseGet(
                () ->
                    Memo.createForPersonalWork(
                        command.content(), command.visitDate(), archivePersonalWork.getId()));

    Memo savedMemo;
    try {
      savedMemo = memoRepository.save(memo);
    } catch (DataIntegrityViolationException e) {
      // 동시 요청으로 두 요청 모두 "기존 메모 없음"으로 판단해 동시에 새로 생성하려는 경우,
      // DB의 유니크 제약(UQ_MEMO_ACTIVE_ARCHIVEPERSONALWORK)이 최종 방어선 역할을 함.
      if (isActiveArchivePersonalWorkUniqueConstraintViolation(e)) {
        throw new BusinessException(MemoErrorCode.MEMO_CONCURRENT_WRITE_CONFLICT, e);
      }
      throw e;
    }
    return toResult(savedMemo);
  }

  private boolean isActiveArchivePersonalWorkUniqueConstraintViolation(
      DataIntegrityViolationException e) {
    String message = e.getMostSpecificCause().getMessage();
    return message != null && message.contains("UQ_MEMO_ACTIVE_ARCHIVEPERSONALWORK");
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
