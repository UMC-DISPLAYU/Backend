package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.ReportCaseDetail;
import com.example.demo.domain.admin.application.result.ReportCasePage;
import com.example.demo.domain.admin.application.result.ReportCaseSummary;
import com.example.demo.domain.admin.presentation.response.ReportCaseResponses;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminReportCaseMapper {
  ReportCaseResponses.Page toResponse(ReportCasePage result);

  ReportCaseResponses.Summary toResponse(ReportCaseSummary result);

  ReportCaseResponses.Detail toResponse(ReportCaseDetail result);

  ReportCaseResponses.Comment toResponse(ReportCaseDetail.Comment result);

  ReportCaseResponses.Parent toResponse(ReportCaseDetail.Parent result);

  ReportCaseResponses.Attachment toResponse(ReportCaseDetail.Attachment result);

  ReportCaseResponses.Report toResponse(ReportCaseDetail.Report result);

  ReportCaseResponses.Resolution toResponse(ReportCaseDetail.Resolution result);
}
