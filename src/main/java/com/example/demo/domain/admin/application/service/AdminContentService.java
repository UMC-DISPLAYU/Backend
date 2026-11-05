package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ContentQueryPort;
import com.example.demo.domain.admin.application.query.ContentSearchQuery;
import com.example.demo.domain.admin.application.result.ContentDetail;
import com.example.demo.domain.admin.application.result.ContentPage;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminContentService {
  private final AdminAccessPort access;
  private final ContentQueryPort contents;

  public ContentPage search(Long adminId, ContentSearchQuery query) {
    requireAdmin(adminId);
    return contents.search(query);
  }

  public ContentDetail getDetail(Long adminId, String contentType, Long contentId) {
    requireAdmin(adminId);
    if (!Set.of("DISPLAY", "ARTWORK").contains(contentType == null ? "" : contentType)
        || contentId == null
        || contentId <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    return contents.getDetail(contentType, contentId);
  }

  private void requireAdmin(Long adminId) {
    if (adminId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(adminId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
  }
}
