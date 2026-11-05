package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.query.UserSearchQuery;
import com.example.demo.domain.admin.application.result.UserDetail;
import com.example.demo.domain.admin.application.result.UserPage;

/** User 도메인 조회 결과를 Admin 조회 계약으로 변환하는 어댑터의 진입점. */
public interface UserQueryPort {
  /**
   * q는 앞뒤 공백을 제거한 최대 100자 검색어이며 null이면 검색하지 않는다. accountStatus는 ACTIVE/SUSPENDED이며 null이면 상태 필터를
   * 적용하지 않는다. cursor는 이전 응답의 양수 nextCursor이며 null이면 첫 페이지, size는 1~100이다. items는 null 대신 빈 목록, 마지막
   * 페이지는 nextCursor=null과 hasNext=false로 반환한다.
   *
   * <p>userId/nickname/accountStatus/createdAt은 실제 회원 정보이며 email은 제공 가능한 값이 없으면 null이다. 시각은
   * Instant로 전달하며 정지와 탈퇴를 같은 상태로 변환하지 않는다.
   *
   * <p>연동 전 확정: 닉네임·이메일 검색의 일치 방식, 정렬·커서 기준, 탈퇴 계정 조회 범위와 개인정보 제공 범위.
   */
  UserPage search(UserSearchQuery query);

  /**
   * 양수인 조회 대상 회원 ID를 받는다. 대상이 없으면 BusinessException(GlobalErrorCode.NOT_FOUND)을 발생시킨다. 기본 필드는 목록과
   * 같은 의미이며 ACTIVE 상태의 suspendedAt/operatorId/reason은 null, 이력이 없으면 history=[]이다. history의
   * operationId는 운영 조치 식별자, operatorId는 처리 관리자 ID, processedAt은 실제 처리 시각이다.
   *
   * <p>연동 전 확정: 이력 action 값, 저장·정렬·조회 범위. 이력 페이징과 연결 작품 조회는 이번 계약에 포함하지 않는다.
   */
  UserDetail getDetail(Long userId);
}
