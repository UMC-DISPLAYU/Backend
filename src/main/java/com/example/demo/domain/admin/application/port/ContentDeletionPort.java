package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.result.ContentDeletionResult;

public interface ContentDeletionPort {
  /**
   * DISPLAY(전시) 또는 ARTWORK(전시 작품)를 운영자 권한으로 삭제한다. 종류와 ID를 함께 사용해 식별하며 개인 작품·섹션·사진 삭제는 제외한다.
   * operatorId는 인증된 관리자 ID, reason은 앞뒤 공백 제거 후 1~1,000자다.
   *
   * <p>연동 구현은 실행 시 대상 존재·삭제 여부(NOT_FOUND), 하위 자료의 실제 소속과 삭제 영향을 재확인한다. 대상별 동시 처리를 직렬화하고 삭제와
   * 운영자·사유·처리 시각의 Admin 이력 저장을 원자적으로 처리한다. operationId는 Admin에서 관리하며 다른 도메인에 생성을 요청하지 않는다. 없는/이미
   * 삭제된 대상은 다시 처리하지 않는다. 처리 불가능한 상태 충돌은 CONFLICT로 전달하며 Admin에서 재시도하지 않는다. expectedVersion과
   * idempotencyKey는 지원하지 않는다.
   *
   * <p>협조 문서에 따라 기존 삭제 이벤트와 연결 자료·반응·댓글·보관함·메모 정리 및 캐시 무효화 흐름을 재사용한다. 기존 커밋 후 비동기 정리의 재시도·실패 기록을
   * 유지하며 Admin에서 정리 이벤트를 중복 발행하지 않는다. 논리/물리 삭제, 원문·이력 보관, 첨부파일 정리 범위는 도메인 연동 시 확인한다. 작성자·대표자 ID를 대신
   * 전달해 기존 삭제 권한 검사를 우회하지 않는다.
   *
   * <p>반환 필드는 모두 필수다. deletedTargetType/Id는 실제 삭제 처리 대상이며 processedAt은 도메인에 저장된 삭제 처리 시각이다. 성공 응답은
   * 대상 삭제 처리의 성공이며 비동기 연결 자료·파일 정리 완료를 의미하지 않는다. 삭제 영향 조회 API는 별도 범위다.
   *
   * <p>shortcut: 실제 삭제·이력 저장과 연결 자료 정리는 후속 Display/Artwork 연동 범위이며, 통합 검증 완료 전에는
   * admin.content-deletion.enabled를 활성화하지 않는다.
   */
  ContentDeletionResult delete(String contentType, Long contentId, Long operatorId, String reason);
}
