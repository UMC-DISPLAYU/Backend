package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.OperationLogPort;
import com.example.demo.domain.admin.application.query.OperationLogSearchQuery;
import com.example.demo.domain.admin.application.result.OperationLogPage;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminOperationLogService {
  private final AdminAccessPort access;
  private final OperationLogPort logs;

  public OperationLogPage search(Long adminId, OperationLogSearchQuery query) {
    if (adminId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(adminId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    return logs.search(query);
  }
}
