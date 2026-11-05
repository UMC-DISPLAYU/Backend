package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ContentStatusPort;
import com.example.demo.domain.admin.application.result.ContentStatusResult;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminContentStatusService {
  private final AdminAccessPort access;
  private final ContentStatusPort contents;

  public ContentStatusResult changeStatus(
      Long operatorId, String contentType, Long contentId, String operationStatus, String reason) {
    if (operatorId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(operatorId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    if (!"DISPLAY".equals(contentType) && !"ARTWORK".equals(contentType)) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    if (contentId == null || contentId <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    if (!"ACTIVE".equals(operationStatus) && !"SUSPENDED".equals(operationStatus)) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    if (reason == null || reason.isBlank()) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    String normalizedReason = reason.strip();
    if (normalizedReason.length() > 1000) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    return contents.changeStatus(
        contentType, contentId, operatorId, operationStatus, normalizedReason);
  }
}
