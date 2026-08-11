package com.example.demo.domain.displaycommunication.application.query;

import com.example.demo.domain.display.application.result.DisplaySummaryResult;
import com.example.demo.domain.display.application.usecase.GetDisplaySummariesUseCase;
import com.example.demo.domain.displaycommunication.application.result.MyDisplayReviewListResult;
import com.example.demo.domain.displaycommunication.application.result.MyDisplayReviewListResult.MyDisplayReviewItemResult;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyDisplayReviewsService {

  private final DisplayReviewPagingPolicy pagingPolicy;
  private final MyDisplayReviewQueryRepository queryRepository;
  private final GetDisplaySummariesUseCase getDisplaySummariesUseCase;

  @Transactional(readOnly = true)
  public MyDisplayReviewListResult getMyReviews(GetMyDisplayReviewsQuery query) {
    int pageSize = pagingPolicy.normalize(query.size());
    List<MyDisplayReviewQueryItem> fetched =
        queryRepository.findByUserIdWithCursor(query.userId(), query.cursorId(), pageSize + 1);

    boolean hasNext = fetched.size() > pageSize;
    List<MyDisplayReviewQueryItem> page = hasNext ? fetched.subList(0, pageSize) : fetched;
    Map<Long, DisplaySummaryResult> displaySummaries =
        getDisplaySummariesUseCase
            .getDisplaySummaries(page.stream().map(MyDisplayReviewQueryItem::displayId).toList())
            .stream()
            .collect(Collectors.toMap(DisplaySummaryResult::displayId, Function.identity()));
    List<MyDisplayReviewItemResult> reviews =
        page.stream()
            .filter(item -> displaySummaries.containsKey(item.displayId()))
            .map(
                item ->
                    MyDisplayReviewItemResult.from(
                        item, displaySummaries.get(item.displayId()).title()))
            .toList();
    Long nextCursorId =
        hasNext && !reviews.isEmpty() ? reviews.get(reviews.size() - 1).displayReviewId() : null;

    return new MyDisplayReviewListResult(reviews, nextCursorId, pageSize, hasNext);
  }
}
