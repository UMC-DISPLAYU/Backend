package com.example.demo.domain.admin.application.port;

/** User 담당자에게 제안하는 역할 조회 계약. 탈퇴·미존재 사용자는 false를 반환한다. */
public interface AdminAccessPort {
  boolean isAdmin(Long userId);
}
