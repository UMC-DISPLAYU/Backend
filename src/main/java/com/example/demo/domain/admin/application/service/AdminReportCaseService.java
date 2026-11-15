package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ReportCaseQueryPort;
import com.example.demo.domain.admin.application.query.ReportCaseSearchQuery;
import com.example.demo.domain.admin.application.result.ReportCaseDetail;
import com.example.demo.domain.admin.application.result.ReportCasePage;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminReportCaseService {
  private final AdminAccessPort access;
  private final ReportCaseQueryPort reports;

  public ReportCasePage search(Long adminId, ReportCaseSearchQuery query) {
    requireAdmin(adminId);
    return reports.search(query);
  }

  public ReportCaseDetail getDetail(Long adminId, Long reportCaseId) {
    requireAdmin(adminId);
    if (reportCaseId == null || reportCaseId <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    return reports.getDetail(reportCaseId);
  }

  private void requireAdmin(Long adminId) {
    if (adminId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(adminId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
  }
}
