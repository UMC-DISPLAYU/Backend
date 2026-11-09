package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.result.ContentStatusResult;

public interface ContentStatusPort {
  /**
   * DISPLAY(전시) 또는 ARTWORK(전시 작품)의 자체 운영 상태를 ACTIVE/SUSPENDED로 변경한다. 개인 작품은 제외하며 contentType과
   * contentId를 함께 사용해 식별한다. operatorId는 인증된 관리자 ID, reason은 앞뒤 공백 제거 후 1~1,000자다.
   *
   * <p>도메인 구현은 대상별 변경을 직렬화하고, 존재·삭제 여부(NOT_FOUND)와 동일 상태 여부(CONFLICT)를 트랜잭션 안에서 검증한다. 상태 변경과
   * 운영자·사유·처리 시각의 이력 저장은 원자적으로 처리하고, 실패 시 둘 다 변경하지 않는다. expectedVersion과 idempotencyKey는 지원하지 않는다.
   *
   * <p>전시 중단·재개는 개별 작품의 자체 운영 상태를 덮어쓰지 않는다. 상위 전시가 중단돼 있어도 작품 자체는 재개할 수 있지만 관람은 계속 차단한다. 심사·게시 상태,
   * 공개 예약과 전시 기간 조건은 변경하지 않으며 재개를 곧바로 공개로 해석하지 않는다.
   *
   * <p>반환값은 변경 후 도메인 결과다. parentOperationStatus는 작품의 상위 전시 운영 상태이며 전시는 null이다. viewerVisible은 자체·상위
   * 상태와 기존 공개 조건을 반영한 최종 관람 가능 여부다. visibilityReasons는 제한 사유 코드 목록이며 제한이 없으면 []이다. 나머지 필드는 모두 필수이고
   * processedAt은 실제 저장된 처리 시각이다. Admin은 공개 여부를 추정하지 않는다.
   *
   * <p>shortcut: 실제 상태·이력 저장, 공개 제한 사유 코드 매핑과 관람 경로 차단은 후속 Display/Artwork 연동 범위이며, 통합 검증 완료 전에는
   * admin.content-status.enabled를 활성화하지 않는다.
   */
  ContentStatusResult changeStatus(
      String contentType, Long contentId, Long operatorId, String operationStatus, String reason);
}
