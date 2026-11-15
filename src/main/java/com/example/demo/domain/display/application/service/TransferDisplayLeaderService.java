package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.command.TransferDisplayLeaderCommand;
import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.result.DisplayLeaderTransferResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.global.error.BusinessException;
import jakarta.persistence.OptimisticLockException;
import java.util.Objects;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferDisplayLeaderService {

  private final DisplayRepository displayRepository;
  private final DisplayPermissionChecker permissionChecker;

  public TransferDisplayLeaderService(
      DisplayRepository displayRepository, DisplayPermissionChecker permissionChecker) {
    this.displayRepository = displayRepository;
    this.permissionChecker = permissionChecker;
  }

  @Transactional
  public DisplayLeaderTransferResult transfer(TransferDisplayLeaderCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    try {
      Display display = findDisplayForUpdate(command.displayId());
      permissionChecker.requireTeamLeader(display, command.requesterUserId());
      Display.LeaderTransfer transfer =
          display.transferLeadership(command.requesterUserId(), command.targetMemberId());
      // flush 시점에 버전 충돌을 확인해 동시 위임이 모두 성공하는 것을 방지한다.
      displayRepository.flush();
      return DisplayLeaderTransferResult.from(display.getId(), transfer);
    } catch (OptimisticLockingFailureException | OptimisticLockException e) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_LEADER_TRANSFER_CONFLICT, e);
    }
  }

  private Display findDisplayForUpdate(Long displayId) {
    return displayRepository
        .findByIdWithOptimisticLock(displayId)
        .filter(display -> !display.isDeleted())
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));
  }
}
