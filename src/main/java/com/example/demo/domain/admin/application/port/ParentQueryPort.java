package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.result.ParentDetail;

/** 후기·방명록을 관리자 검토용 응답으로 변환하는 조회 어댑터의 진입점. */
public interface ParentQueryPort {
  /**
   * parentType과 양수 parentId로 상위 자료를 조회한다.
   *
   * <ul>
   *   <li>지원하지 않는 parentType은 INVALID_INPUT_VALUE로 처리한다.
   *   <li>대상이 없으면 NOT_FOUND로 처리한다.
   *   <li>삭제된 자료도 관리자 검토에 필요한 보관 정보가 있으면 반환한다.
   *   <li>탈퇴 사용자처럼 보관하지 않는 작성자 정보는 null로 반환한다.
   *   <li>displayId/artworkId는 해당하지 않으면 null이다.
   *   <li>parentType/첨부 종류는 소유 도메인 코드를 그대로 사용한다.
   *   <li>attachments는 값이 없으면 빈 배열로 반환한다.
   * </ul>
   */
  ParentDetail getParentDetail(String parentType, Long parentId);
}
