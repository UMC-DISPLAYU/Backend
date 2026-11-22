package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.query.CommentReportSearchQuery;
import com.example.demo.domain.admin.application.result.CommentReportPage;

public interface CommentReportQueryPort {
  CommentReportPage search(String commentType, Long commentId, CommentReportSearchQuery query);
}
