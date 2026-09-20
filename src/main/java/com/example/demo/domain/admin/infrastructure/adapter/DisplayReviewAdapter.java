package com.example.demo.domain.admin.infrastructure.adapter;

import com.example.demo.domain.admin.application.port.DisplayReviewPort;
import com.example.demo.domain.admin.application.query.ReviewSearchQuery;
import com.example.demo.domain.admin.application.result.ReviewDetail;
import com.example.demo.domain.admin.application.result.ReviewPage;
import com.example.demo.domain.admin.application.result.ReviewSummary;
import com.example.demo.domain.display.application.command.ApproveDisplayScreeningService;
import com.example.demo.domain.display.application.command.RejectDisplayScreeningService;
import com.example.demo.domain.display.application.query.DisplayScreeningSearchQuery;
import com.example.demo.domain.display.application.query.GetDisplayScreeningDetailService;
import com.example.demo.domain.display.application.query.SearchDisplayScreeningsService;
import com.example.demo.domain.display.application.result.DisplayScreeningDetailResult;
import com.example.demo.domain.display.application.result.DisplayScreeningPageResult;
import com.example.demo.domain.display.application.result.DisplayScreeningSummaryResult;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import org.springframework.stereotype.Component;

@Component
public class DisplayReviewAdapter implements DisplayReviewPort {

  private final SearchDisplayScreeningsService searchService;
  private final GetDisplayScreeningDetailService detailService;
  private final ApproveDisplayScreeningService approveService;
  private final RejectDisplayScreeningService rejectService;

  public DisplayReviewAdapter(
      SearchDisplayScreeningsService searchService,
      GetDisplayScreeningDetailService detailService,
      ApproveDisplayScreeningService approveService,
      RejectDisplayScreeningService rejectService) {
    this.searchService = searchService;
    this.detailService = detailService;
    this.approveService = approveService;
    this.rejectService = rejectService;
  }

  @Override
  public ReviewPage search(ReviewSearchQuery query) {
    DisplayScreeningPageResult result =
        searchService.search(
            new DisplayScreeningSearchQuery(
                DisplayStatus.valueOf(query.status()), query.cursor(), query.size()));
    return new ReviewPage(
        result.items().stream().map(this::toSummary).toList(),
        result.nextCursor(),
        result.hasNext());
  }

  @Override
  public ReviewDetail getDetail(Long displayId) {
    DisplayScreeningDetailResult result = detailService.getDetail(displayId);
    return new ReviewDetail(
        result.displayId(),
        result.title(),
        result.subtitle(),
        result.content(),
        result.requesterId(),
        result.status(),
        result.requestedAt(),
        result.processedAt(),
        result.reviewerId(),
        result.rejectionReason(),
        result.startDate(),
        result.endDate(),
        result.location(),
        result.imageUrls(),
        result.school(),
        result.leaderName());
  }

  @Override
  public void approve(Long displayId, Long reviewerId) {
    approveService.approve(displayId, reviewerId);
  }

  @Override
  public void reject(Long displayId, Long reviewerId, String reason) {
    rejectService.reject(displayId, reviewerId, reason);
  }

  private ReviewSummary toSummary(DisplayScreeningSummaryResult result) {
    return new ReviewSummary(
        result.displayId(),
        result.title(),
        result.requesterId(),
        result.status(),
        result.requestedAt(),
        result.school(),
        result.leaderName(),
        result.startDate(),
        result.endDate(),
        result.posterImageUrl());
  }
}
