package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.result.UserStatusResult;

public interface UserStatusPort {
  /**
   * 일반 회원의 상태를 ACTIVE/SUSPENDED로 변경한다. operatorId는 인증된 관리자이며 reason은 앞뒤 공백 제거 후 1~1,000자다. 종료일 없이
   * 수동 해제하고 작품·팀·작가 연결과 비회원 감상은 유지한다.
   *
   * <p>User 구현은 회원별 변경을 직렬화하고, 대상 존재·탈퇴 여부(NOT_FOUND), 본인·ADMIN 대상 여부(FORBIDDEN), 동일 상태
   * 여부(CONFLICT)를 트랜잭션 안에서 재검증한다. 실패 시 상태·이력을 변경하지 않는다. 실제 상태 변경과 운영자·사유·처리 시각의 이력 저장은 원자적으로 처리한다.
   * 반환값은 저장된 회원 ID·상태·처리 시각이며 모두 필수다. expectedVersion과 idempotencyKey는 지원하지 않는다.
   *
   * <p>shortcut: 실제 User 어댑터와 정지 계정의 로그인·재발급·기존 토큰 차단은 후속 연동 범위이며, 완료 전에는
   * admin.user-status.enabled를 활성화하지 않는다.
   */
  UserStatusResult changeStatus(Long userId, Long operatorId, String accountStatus, String reason);
}
