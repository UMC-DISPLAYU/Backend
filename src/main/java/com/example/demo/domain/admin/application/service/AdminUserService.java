package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.UserQueryPort;
import com.example.demo.domain.admin.application.query.UserSearchQuery;
import com.example.demo.domain.admin.application.result.UserDetail;
import com.example.demo.domain.admin.application.result.UserPage;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminUserService {
  private final AdminAccessPort access;
  private final UserQueryPort users;

  public UserPage search(Long adminId, UserSearchQuery query) {
    requireAdmin(adminId);
    return users.search(query);
  }

  public UserDetail getDetail(Long adminId, Long userId) {
    requireAdmin(adminId);
    if (userId == null || userId <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    return users.getDetail(userId);
  }

  private void requireAdmin(Long adminId) {
    if (adminId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(adminId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
  }
}
