package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ParentQueryPort;
import com.example.demo.domain.admin.application.result.ParentDetail;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminParentService {
  private static final Pattern TYPE_CODE = Pattern.compile("[A-Z][A-Z0-9_]{0,49}");

  private final AdminAccessPort access;
  private final ParentQueryPort parents;

  public ParentDetail getDetail(Long adminId, String parentType, Long parentId) {
    requireAdmin(adminId);
    if (parentType == null
        || !TYPE_CODE.matcher(parentType).matches()
        || parentId == null
        || parentId <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    return parents.getParentDetail(parentType, parentId);
  }

  private void requireAdmin(Long adminId) {
    if (adminId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(adminId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
  }
}
