package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.CommentQueryPort;
import com.example.demo.domain.admin.application.result.CommentDetail;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminCommentService {
  private static final Pattern TYPE_CODE = Pattern.compile("[A-Z][A-Z0-9_]{0,49}");

  private final AdminAccessPort access;
  private final CommentQueryPort comments;

  public CommentDetail getDetail(Long adminId, String commentType, Long commentId) {
    requireAdmin(adminId);
    if (commentType == null
        || !TYPE_CODE.matcher(commentType).matches()
        || commentId == null
        || commentId <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    return comments.getCommentDetail(commentType, commentId);
  }

  private void requireAdmin(Long adminId) {
    if (adminId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(adminId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
  }
}
