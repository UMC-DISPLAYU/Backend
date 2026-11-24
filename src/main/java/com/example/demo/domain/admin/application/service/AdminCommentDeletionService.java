package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.CommentDeletionPort;
import com.example.demo.domain.admin.application.port.OperationLogPort;
import com.example.demo.domain.admin.application.result.CommentDeletionResult;
import com.example.demo.domain.admin.application.result.OperationLog;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class AdminCommentDeletionService {
  private static final String DELETE = "DELETE";
  private static final Set<String> SUPPORTED_TYPES =
      Set.of("DISPLAY_REVIEW_REPLY", "ARTWORK_FEELING_REPLY", "PERSONAL_ARTWORK_FEELING_REPLY");

  private final AdminAccessPort access;
  private final CommentDeletionPort comments;
  private final OperationLogPort logs;

  /** 삭제와 운영 이력 저장을 한 트랜잭션으로 처리한다. 이력 저장이 실패하면 삭제도 롤백된다. */
  @Transactional
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
    CommentDeletionResult result =
        comments.delete(commentType, commentId, operatorId, normalizedReason);
    logs.record(
        OperationLog.of(
            commentType, commentId, DELETE, operatorId, normalizedReason, result.processedAt()));
    return result;
  }

  private void requireAdmin(Long operatorId) {
    if (operatorId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(operatorId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
  }
}
