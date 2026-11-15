package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.ReportResolutionResult;
import com.example.demo.domain.admin.presentation.response.ReportResolutionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminReportResolutionMapper {
  ReportResolutionResponse toResponse(ReportResolutionResult result);
}
