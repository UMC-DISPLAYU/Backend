package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.query.ContentSearchQuery;
import com.example.demo.domain.admin.application.result.ContentDetail;
import com.example.demo.domain.admin.application.result.ContentPage;

/** Display/Artwork 도메인 조회 결과를 Admin 계약으로 변환하는 어댑터의 진입점. */
public interface ContentQueryPort {
  /**
   * DISPLAY는 전시, ARTWORK는 전시에 속한 작품이다. 개인 작품은 포함하지 않으며 contentType과 contentId를 함께 사용해 식별한다. 공개 전·운영
   * 중단 자료도 관리자에게 조회하고, 결과가 없으면 items=[], 마지막 페이지는 nextCursor=null, hasNext=false로 반환한다.
   *
   * <p>q는 앞뒤 공백을 제거한 최대 100자 검색어이며 null이면 검색하지 않는다. 종류·운영 상태 필터도 null이면 전체다. size는 1~100(HTTP 기본
   * 20), cursor=null이면 첫 페이지다. cursor/nextCursor는 최대 1024자의 비어 있지 않은 문자열 토큰이며 Admin과 프론트는 이를
   * 해석·변형하지 않는다. 검색·필터 변경 시 커서를 초기화한다.
   *
   * <p>어댑터가 정렬 기준·종류·ID를 구분하는 토큰을 생성·해석하고, 잘못된 토큰은
   * BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE)로 처리한다. 서로 다른 종류의 동일 숫자 ID를 누락하거나 중복시키지
   * 않는 안정적인 순서가 필요하다. 토큰 형식·정렬 기준과 조회 조건 검증은 실제 연동 시 확정한다.
   *
   * <p>operationStatus는 대상 자체의 운영 상태(ACTIVE/SUSPENDED)다. parentOperationStatus는 작품의 부모 전시 운영 상태이며
   * 전시는 null이다. viewerVisible은 부모 중단·공개 예약 등을 적용한 최종 공개 여부이고, visibilityReasons는 제한 사유 목록으로 사유가 없으면
   * []이다. Admin에서 운영 상태로 공개 여부를 추정하지 않는다.
   *
   * <p>ownerUserId/ownerName은 실제 원본에서 확인된 값만 제공하고 식별할 수 없으면 null이다. 작성자·대표자·작가를 임의로 대체하지 않으며
   * thumbnailUrl은 대표 이미지가 없으면 null, createdAt은 실제 생성 시각이다.
   *
   * <p>연동 전 확정: 콘텐츠명·작성자 표시명 검색의 일치 방식, 원본 필드 매핑, 공개 제한 사유 코드와 삭제 자료 조회 범위.
   */
  ContentPage search(ContentSearchQuery query);

  /**
   * 종류와 양수 ID에 해당하는 대상이 없으면 BusinessException(GlobalErrorCode.NOT_FOUND)을 발생시킨다. 기본 필드는 목록과 같은 의미이며
   * imageUrls/history는 값이 없으면 []이다. 초대 토큰은 제공하지 않는다. history.operationId는 Admin 운영 이력 ID,
   * operatorId는 처리 관리자 ID, processedAt은 실제 처리 시각이다.
   *
   * <p>연동 전 확정: 상세 원본 필드와 공동 작가 정보 매핑, 이력 action·저장·정렬·조회 범위. 이력 페이징과 삭제 영향 조회는 이번 계약에 포함하지 않는다.
   */
  ContentDetail getDetail(String contentType, Long contentId);
}
