package com.example.demo.domain.admin.presentation.mapper;

import com.example.demo.domain.admin.application.result.ReviewDetail;
import com.example.demo.domain.admin.application.result.ReviewPage;
import com.example.demo.domain.admin.application.result.ReviewSummary;
import com.example.demo.domain.admin.presentation.response.ReviewResponses;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminReviewMapper {
  ReviewResponses.Page toResponse(ReviewPage result);

  ReviewResponses.Summary toResponse(ReviewSummary result);

  ReviewResponses.Detail toResponse(ReviewDetail result);
}
