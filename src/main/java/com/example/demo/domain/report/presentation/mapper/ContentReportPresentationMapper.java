package com.example.demo.domain.report.presentation.mapper;

import com.example.demo.domain.report.application.command.ReportArtworkFeelingCommand;
import com.example.demo.domain.report.application.command.ReportCommentCommand;
import com.example.demo.domain.report.application.command.ReportDisplayReviewCommand;
import com.example.demo.domain.report.application.result.CreateCommentReportResult;
import com.example.demo.domain.report.application.result.CreateContentReportResult;
import com.example.demo.domain.report.presentation.request.CreateCommentReportRequest;
import com.example.demo.domain.report.presentation.request.CreateContentReportRequest;
import com.example.demo.domain.report.presentation.response.CreateCommentReportResponse;
import com.example.demo.domain.report.presentation.response.CreateContentReportResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContentReportPresentationMapper {
  ReportDisplayReviewCommand toReviewCommand(
      Long displayId, Long reviewId, Long reporterUserId, CreateContentReportRequest request);

  ReportArtworkFeelingCommand toFeelingCommand(
      Long artworkId, Long feelingId, Long reporterUserId, CreateContentReportRequest request);

  default ReportCommentCommand toCommentCommand(
      String commentType, Long commentId, Long reporterUserId, CreateCommentReportRequest request) {
    return new ReportCommentCommand(
        commentType, commentId, reporterUserId, request.reasonCode(), request.description());
  }

  CreateContentReportResponse toResponse(CreateContentReportResult result);

  CreateCommentReportResponse toCommentResponse(CreateCommentReportResult result);
}
