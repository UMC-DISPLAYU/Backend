package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.result.ReportResolutionResult;

/** 신고 처리와 댓글 도메인 연동 계약. 실제 저장·삭제의 통합 검증 전에는 활성화하지 않는다. */
public interface ReportResolutionPort {
  /**
   * 묶음에서 댓글 식별자를 조회하며 요청자가 지정한 댓글/작성자 ID로 대체하지 않는다. NO_ACTION은 댓글을 유지하고 종결한다. DELETE_COMMENT는 해당
   * 댓글을 삭제하고 종결한다. 부모 게시글·전시·작품 삭제와 회원 정지는 포함하지 않는다. 없는 묶음은 NOT_FOUND, 이미 종결된 묶음 또는 이미 삭제된 댓글의
   * DELETE_COMMENT는 CONFLICT다. NO_ACTION은 댓글이 삭제돼 있어도 미종결 묶음이면 처리할 수 있다. 묶음별 처리를 직렬화하고 실행 시 상태를
   * 재확인한다. 댓글 삭제·묶음 종결·Admin 이력 저장을 한 트랜잭션으로 수행하며 실패 시 모두 롤백한다. 별도 삭제 후 종결 호출로 대체하지 않는다. 기존 댓글 삭제
   * 정리 흐름을 재사용하고 관리자 ID를 작성자 ID로 위장하지 않는다. operatorId는 인증된 관리자, reason은 검증·공백 제거한 값이다. 버전·중복 방지 키는
   * 사용하지 않는다. shortcut: 처리 중 신규 신고 유입·재신고 회차·대댓글 정리 범위는 실제 어댑터 연동 전에 확정한다.
   */
  ReportResolutionResult resolve(Long reportCaseId, String action, Long operatorId, String reason);
}
