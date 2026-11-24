package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.CommentDetail;
import com.example.demo.domain.admin.application.result.CommentReportPage;
import com.example.demo.domain.admin.presentation.response.CommentResponses;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminCommentMapper {
  CommentResponses.Detail toResponse(CommentDetail result);

  CommentResponses.Comment toResponse(CommentDetail.Comment result);

  CommentResponses.Parent toResponse(CommentDetail.Parent result);

  CommentResponses.Attachment toResponse(CommentDetail.Attachment result);

  CommentResponses.ReportPage toResponse(CommentReportPage result);

  CommentResponses.Report toResponse(CommentReportPage.Item result);
}
