package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.query.OperationLogSearchQuery;
import com.example.demo.domain.admin.application.result.OperationLog;
import com.example.demo.domain.admin.application.result.OperationLogPage;

/** 관리자 운영 이력 저장·조회. */
public interface OperationLogPort {
  /**
   * 조치와 같은 트랜잭션 안에서 호출한다. 저장에 실패하면 예외를 던져 조치도 함께 롤백되게 한다. 저장한 이력은 수정·삭제하지 않는다.
   *
   * <ul>
   *   <li>targetType: 조치 대상의 소유 도메인 코드(예: DISPLAY_REVIEW_REPLY)
   *   <li>action: 조치 코드(예: DELETE)
   *   <li>reason: 검증·공백 제거한 조치 사유
   *   <li>processedAt: 도메인에 저장된 처리 시각
   * </ul>
   */
  OperationLog record(OperationLog log);

  /** operationId 내림차순, cursor 미만 ID를 size개 반환한다. 결과가 없으면 items=[], nextCursor=null. */
  OperationLogPage search(OperationLogSearchQuery query);
}
