package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.result.ReportResolutionResult;

/** 댓글 신고 묶음 종결. */
public interface ReportResolutionPort {
  /**
   * 묶음에서 댓글 식별자를 조회하며 요청자가 지정한 댓글/작성자 ID로 대체하지 않는다.
   *
   * <ul>
   *   <li>NO_ACTION은 댓글을 유지하고 종결한다. 댓글이 이미 삭제돼 있어도 미종결 묶음이면 처리할 수 있다.
   *   <li>DELETE_COMMENT는 기존 댓글 삭제를 재사용해 삭제하고 종결한다. 관리자 ID를 작성자 ID로 위장하지 않는다.
   *   <li>부모 게시글·전시·작품 삭제와 회원 정지는 포함하지 않는다.
   *   <li>없는 묶음은 NOT_FOUND, 이미 종결된 묶음 또는 이미 삭제된 댓글의 DELETE_COMMENT는 CONFLICT다.
   *   <li>처리 시점까지 접수된 회차 신고만 종결하고, 이후 접수된 신고는 다음 회차가 된다.
   *   <li>댓글 삭제와 묶음 종결을 한 트랜잭션으로 수행하며 실패 시 모두 롤백한다. 같은 묶음의 동시 종결은 묶음 ID 키 충돌로 CONFLICT가 된다.
   *   <li>operatorId는 인증된 관리자, reason은 검증·공백 제거한 값이다. 버전·중복 방지 키는 사용하지 않는다.
   * </ul>
   *
   * <p>shortcut: 종결의 운영 이력(AdminOperation) 연결은 운영 이력 PR(#660) 머지 후 반영한다.
   */
  ReportResolutionResult resolve(Long reportCaseId, String action, Long operatorId, String reason);
}
