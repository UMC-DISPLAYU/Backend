package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.CommentQueryPort;
import com.example.demo.domain.admin.application.port.CommentReportQueryPort;
import com.example.demo.domain.admin.application.query.CommentReportSearchQuery;
import com.example.demo.domain.admin.application.result.CommentDetail;
import com.example.demo.domain.admin.application.result.CommentReportPage;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminCommentService {
  private static final Pattern TYPE_CODE = Pattern.compile("[A-Z][A-Z0-9_]{0,49}");
  private static final Set<String> REPORTABLE_TYPES =
      Set.of("DISPLAY_REVIEW_REPLY", "ARTWORK_FEELING_REPLY", "PERSONAL_ARTWORK_FEELING_REPLY");

  private final AdminAccessPort access;
  private final CommentQueryPort comments;
  private final CommentReportQueryPort reports;

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

  public CommentReportPage getReports(
      Long adminId, String commentType, Long commentId, CommentReportSearchQuery query) {
    requireAdmin(adminId);
    if (commentType == null
        || !REPORTABLE_TYPES.contains(commentType)
        || commentId == null
        || commentId <= 0
        || query == null) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    comments.getCommentDetail(commentType, commentId);
    return reports.search(commentType, commentId, query);
  }

  private void requireAdmin(Long adminId) {
    if (adminId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(adminId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
  }
}
