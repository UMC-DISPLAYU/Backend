package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.CommentDeletionPort;
import com.example.demo.domain.admin.application.result.CommentDeletionResult;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminCommentDeletionService {
  private static final Set<String> SUPPORTED_TYPES =
      Set.of("DISPLAY_REVIEW_REPLY", "ARTWORK_FEELING_REPLY", "PERSONAL_ARTWORK_FEELING_REPLY");

  private final AdminAccessPort access;
  private final CommentDeletionPort comments;

  public CommentDeletionResult delete(
      Long operatorId, String commentType, Long commentId, String reason) {
    requireAdmin(operatorId);
    if (commentType == null
        || !SUPPORTED_TYPES.contains(commentType)
        || commentId == null
        || commentId <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    if (reason == null || reason.isBlank()) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    String normalizedReason = reason.strip();
    if (normalizedReason.length() > 1000) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    return comments.delete(commentType, commentId, operatorId, normalizedReason);
  }

  private void requireAdmin(Long operatorId) {
    if (operatorId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(operatorId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
  }
}
