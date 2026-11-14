package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.query.ReportCaseSearchQuery;
import com.example.demo.domain.admin.application.result.ReportCaseDetail;
import com.example.demo.domain.admin.application.result.ReportCasePage;

/** 신고 묶음 조회 계약. 실제 접수·묶음 저장소 연동 및 검증 후 활성화한다. */
public interface ReportCaseQueryPort {
  /**
   *
   *
   * <ul>
   *   <li>댓글 신고 묶음만 조회한다.
   *   <li>reportCaseId 내림차순, cursor 미만 ID를 size개 반환한다.
   *   <li>다음 페이지가 없으면 nextCursor=null, hasNext=false이며 items는 빈 배열을 허용한다.
   *   <li>reportCount/lastReportedAt는 해당 묶음의 접수 범위 기준이며 reportId와 묶음 ID를 혼용하지 않는다.
   * </ul>
   */
  ReportCasePage search(ReportCaseSearchQuery query);

  /**
   *
   *
   * <ul>
   *   <li>없는 묶음은 NOT_FOUND. 삭제된 댓글도 묶음이 남아 있으면 상세를 제공한다.
   *   <li>comment/parent는 현재 자료이며 신고 당시 스냅샷으로 간주하지 않는다.
   *   <li>보관하지 않는 원문·탈퇴 사용자 정보는 null로 제공한다.
   *   <li>parent/displayId/artworkId는 해당하지 않으면 null이다.
   *   <li>commentType/parentType/reasonCode/첨부 종류는 소유 도메인 코드이며 Admin에서 추정하지 않는다.
   *   <li>처리 전 resolution=null. reports와 attachments는 값이 없으면 []이다.
   * </ul>
   *
   * <p>연동 전 확정할 것
   *
   * <ul>
   *   <li>shortcut: 묶음 생성·재신고 회차·접수 내역 전체 반환 범위는 미정이며 실제 어댑터 연동 전에 확정한다.
   * </ul>
   */
  ReportCaseDetail getDetail(Long reportCaseId);
}
