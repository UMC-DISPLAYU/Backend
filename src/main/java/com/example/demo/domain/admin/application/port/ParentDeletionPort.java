package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.result.ParentDeletionResult;

/** 운영자가 후기·방명록과 하위 댓글을 삭제하는 진입점. */
public interface ParentDeletionPort {
  /**
   * 지원하는 parentType의 활성 상위 자료와 활성 하위 댓글을 논리 삭제한다. 없는 자료와 이미 삭제된 자료는 NOT_FOUND로 처리한다. operatorId는
   * 인증된 관리자 ID이며 reason은 앞뒤 공백이 제거된 1~1,000자다.
   */
  ParentDeletionResult delete(String parentType, Long parentId, Long operatorId, String reason);
}
