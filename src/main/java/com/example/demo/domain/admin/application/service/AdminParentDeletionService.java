package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ParentDeletionPort;
import com.example.demo.domain.admin.application.result.ParentDeletionResult;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminParentDeletionService {
  private static final Set<String> SUPPORTED_TYPES =
      Set.of("DISPLAY_REVIEW", "ARTWORK_FEELING", "PERSONAL_ARTWORK_FEELING");

  private final AdminAccessPort access;
  private final ParentDeletionPort parents;

  public ParentDeletionResult delete(
      Long operatorId, String parentType, Long parentId, String reason) {
    requireAdmin(operatorId);
    if (parentType == null
        || !SUPPORTED_TYPES.contains(parentType)
        || parentId == null
        || parentId <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    if (reason == null || reason.isBlank()) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    String normalizedReason = reason.strip();
    if (normalizedReason.length() > 1000) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    return parents.delete(parentType, parentId, operatorId, normalizedReason);
  }

  private void requireAdmin(Long operatorId) {
    if (operatorId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(operatorId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
  }
}
