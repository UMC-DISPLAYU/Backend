package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.command.ChangeDisplayMemberRoleCommand;
import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.result.DisplayMemberRoleResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.global.error.BusinessException;
import jakarta.persistence.OptimisticLockException;
import java.util.Objects;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeDisplayMemberRoleService {

  private final DisplayRepository displayRepository;
  private final DisplayPermissionChecker permissionChecker;

  public ChangeDisplayMemberRoleService(
      DisplayRepository displayRepository, DisplayPermissionChecker permissionChecker) {
    this.displayRepository = displayRepository;
    this.permissionChecker = permissionChecker;
  }

  @Transactional
  public DisplayMemberRoleResult changeRole(ChangeDisplayMemberRoleCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    try {
      Display display = findDisplayForUpdate(command.displayId());
      permissionChecker.requireTeamLeader(display, command.requesterUserId());
      TeamMember member = display.changeMemberRole(command.teamMemberId(), command.role());
      displayRepository.flush();
      return DisplayMemberRoleResult.from(display.getId(), member);
    } catch (OptimisticLockingFailureException | OptimisticLockException e) {
      throw new BusinessException(DisplayErrorCode.INVALID_DISPLAY_MEMBER_ROLE_CHANGE, e);
    }
  }

  private Display findDisplayForUpdate(Long displayId) {
    return displayRepository
        .findByIdWithOptimisticLock(displayId)
        .filter(display -> !display.isDeleted())
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));
  }
}
