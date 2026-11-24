package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.query.ReportCaseSearchQuery;
import com.example.demo.domain.admin.application.result.ReportCaseDetail;
import com.example.demo.domain.admin.application.result.ReportCasePage;

/** 댓글 신고 묶음 조회. 접수 내역(ContentReport)과 종결 결과(AdminReportCase)로 계산한다. */
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
   * <p>묶음 기준
   *
   * <ul>
   *   <li>묶음은 댓글 하나(commentType+commentId)에 쌓인 미종결 신고 모음이며, 묶음 ID는 그 회차 첫 신고의 reportId다.
   *   <li>종결 뒤 같은 댓글에 들어온 신고는 새 묶음(다음 회차)이 된다. 종결된 묶음의 범위와 건수는 바뀌지 않는다.
   *   <li>reports는 해당 회차의 접수 내역 전체를 reportId 오름차순으로 반환한다.
   *   <li>회차 첫 신고가 아닌 reportId는 묶음 ID가 아니므로 NOT_FOUND다.
   * </ul>
   */
  ReportCaseDetail getDetail(Long reportCaseId);
}
