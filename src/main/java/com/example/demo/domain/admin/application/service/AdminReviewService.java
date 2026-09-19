package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.DisplayReviewPort;
import com.example.demo.domain.admin.application.query.ReviewSearchQuery;
import com.example.demo.domain.admin.application.result.ReviewDetail;
import com.example.demo.domain.admin.application.result.ReviewPage;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminReviewService {
  private final AdminAccessPort access;
  private final DisplayReviewPort reviews;

  public ReviewPage search(Long userId, ReviewSearchQuery query) {
    requireAdmin(userId);
    return reviews.search(query);
  }

  public ReviewDetail getDetail(Long userId, Long displayId) {
    requireAdmin(userId);
    requireDisplayId(displayId);
    return reviews.getDetail(displayId);
  }

  public void approve(Long userId, Long displayId) {
    requireAdmin(userId);
    requireDisplayId(displayId);
    reviews.approve(displayId, userId);
  }

  public void reject(Long userId, Long displayId, String reason) {
    requireAdmin(userId);
    requireDisplayId(displayId);
    if (reason == null || reason.isBlank() || reason.length() > 1000) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    reviews.reject(displayId, userId, reason.strip());
  }

  private void requireAdmin(Long userId) {
    if (userId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(userId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
  }

  private void requireDisplayId(Long displayId) {
    if (displayId == null || displayId <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
  }
}
