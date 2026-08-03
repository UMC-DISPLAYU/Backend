package com.example.demo.domain.displaycommunication.application.query;

import com.example.demo.domain.displaycommunication.application.result.MyDisplayReviewListResult;
import com.example.demo.domain.displaycommunication.application.result.MyDisplayReviewListResult.MyDisplayReviewItemResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMyDisplayReviewsService {

  private final DisplayReviewPagingPolicy pagingPolicy;
  private final MyDisplayReviewQueryRepository queryRepository;

  public MyDisplayReviewListResult getMyReviews(GetMyDisplayReviewsQuery query) {
    int pageSize = pagingPolicy.normalize(query.size());
    List<MyDisplayReviewQueryItem> fetched =
        queryRepository.findByUserIdWithCursor(query.userId(), query.cursorId(), pageSize + 1);

    boolean hasNext = fetched.size() > pageSize;
    List<MyDisplayReviewQueryItem> page = hasNext ? fetched.subList(0, pageSize) : fetched;
    List<MyDisplayReviewItemResult> reviews =
        page.stream().map(MyDisplayReviewItemResult::from).toList();
    Long nextCursorId =
        hasNext && !reviews.isEmpty() ? reviews.get(reviews.size() - 1).displayReviewId() : null;

    return new MyDisplayReviewListResult(reviews, nextCursorId, pageSize, hasNext);
  }
}
