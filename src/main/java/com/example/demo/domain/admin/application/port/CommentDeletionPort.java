package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.result.CommentDeletionResult;

/** 운영자가 후기·방명록 댓글을 삭제하는 진입점. */
public interface CommentDeletionPort {
  /**
   * 지원하는 commentType의 활성 댓글을 논리 삭제한다. 없는 댓글과 이미 삭제된 댓글은 NOT_FOUND로 처리한다. operatorId는 인증된 관리자 ID이며
   * reason은 앞뒤 공백이 제거된 1~1,000자다.
   */
  CommentDeletionResult delete(String commentType, Long commentId, Long operatorId, String reason);
}
