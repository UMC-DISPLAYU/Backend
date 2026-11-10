package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.UserStatusPort;
import com.example.demo.domain.admin.application.result.UserStatusResult;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminUserStatusService {
  private final AdminAccessPort access;
  private final UserStatusPort users;

  public UserStatusResult changeStatus(
      Long operatorId, Long userId, String accountStatus, String reason) {
    if (operatorId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(operatorId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    if (userId == null || userId <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    if (!"ACTIVE".equals(accountStatus) && !"SUSPENDED".equals(accountStatus)) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    if (reason == null || reason.isBlank()) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    String normalizedReason = reason.strip();
    if (normalizedReason.length() > 1000) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    if (operatorId.equals(userId) || access.isAdmin(userId)) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }
    return users.changeStatus(userId, operatorId, accountStatus, normalizedReason);
  }
}
