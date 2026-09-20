package com.example.demo.domain.admin.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.admin.application.query.ReviewSearchQuery;
import com.example.demo.domain.admin.application.result.ReviewDetail;
import com.example.demo.domain.admin.application.result.ReviewPage;
import com.example.demo.domain.display.application.command.ApproveDisplayScreeningService;
import com.example.demo.domain.display.application.command.RejectDisplayScreeningService;
import com.example.demo.domain.display.application.query.DisplayScreeningSearchQuery;
import com.example.demo.domain.display.application.query.GetDisplayScreeningDetailService;
import com.example.demo.domain.display.application.query.SearchDisplayScreeningsService;
import com.example.demo.domain.display.application.result.DisplayScreeningDetailResult;
import com.example.demo.domain.display.application.result.DisplayScreeningPageResult;
import com.example.demo.domain.display.application.result.DisplayScreeningSummaryResult;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class DisplayReviewAdapterTest {

  private final SearchDisplayScreeningsService searchService =
      mock(SearchDisplayScreeningsService.class);
  private final GetDisplayScreeningDetailService detailService =
      mock(GetDisplayScreeningDetailService.class);
  private final ApproveDisplayScreeningService approveService =
      mock(ApproveDisplayScreeningService.class);
  private final RejectDisplayScreeningService rejectService =
      mock(RejectDisplayScreeningService.class);
  private final DisplayReviewAdapter adapter =
      new DisplayReviewAdapter(searchService, detailService, approveService, rejectService);

  @Test
  void searchMapsAdminContractToDisplayUseCase() {
    DisplayScreeningSearchQuery displayQuery =
        new DisplayScreeningSearchQuery(DisplayStatus.PENDING_REVIEW, 10L, 2);
    DisplayScreeningSummaryResult item =
        new DisplayScreeningSummaryResult(
            9L,
            "전시",
            1L,
            "PENDING_REVIEW",
            Instant.parse("2026-09-20T01:00:00Z"),
            "학교",
            "대표자",
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 10, 7),
            "poster");
    when(searchService.search(displayQuery))
        .thenReturn(new DisplayScreeningPageResult(List.of(item), 9L, true));

    ReviewPage result = adapter.search(new ReviewSearchQuery("PENDING_REVIEW", 10L, 2));

    assertThat(result.items()).hasSize(1);
    assertThat(result.items().getFirst().leaderName()).isEqualTo("대표자");
    assertThat(result.nextCursor()).isEqualTo(9L);
    assertThat(result.hasNext()).isTrue();
    verify(searchService).search(displayQuery);
  }

  @Test
  void detailAndCommandsDelegateWithoutChangingContractValues() {
    Instant requestedAt = Instant.parse("2026-09-20T01:00:00Z");
    Instant processedAt = Instant.parse("2026-09-20T02:00:00Z");
    when(detailService.getDetail(3L))
        .thenReturn(
            new DisplayScreeningDetailResult(
                3L,
                "전시",
                "부제",
                "내용",
                1L,
                "REJECTED",
                requestedAt,
                processedAt,
                7L,
                "일정 수정",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 7),
                "전시장",
                List.of("poster"),
                "학교",
                null));

    ReviewDetail detail = adapter.getDetail(3L);
    adapter.approve(4L, 7L);
    adapter.reject(5L, 7L, "보완 필요");

    assertThat(detail.requestedAt()).isEqualTo(requestedAt);
    assertThat(detail.processedAt()).isEqualTo(processedAt);
    assertThat(detail.rejectionReason()).isEqualTo("일정 수정");
    assertThat(detail.leaderName()).isNull();
    verify(approveService).approve(4L, 7L);
    verify(rejectService).reject(5L, 7L, "보완 필요");
  }
}
