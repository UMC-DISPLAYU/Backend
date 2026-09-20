package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.application.result.DisplayScreeningPageResult;
import com.example.demo.domain.display.application.result.DisplayScreeningSummaryResult;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchDisplayScreeningsService {

  private final DisplayScreeningQueryRepository queryRepository;

  public SearchDisplayScreeningsService(DisplayScreeningQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Transactional(readOnly = true)
  public DisplayScreeningPageResult search(DisplayScreeningSearchQuery query) {
    List<DisplayScreeningSummaryQueryResult> fetched =
        queryRepository.search(query, query.size() + 1);
    boolean hasNext = fetched.size() > query.size();
    List<DisplayScreeningSummaryResult> items =
        fetched.stream().limit(query.size()).map(DisplayScreeningSummaryResult::from).toList();
    Long nextCursor = hasNext && !items.isEmpty() ? items.getLast().displayId() : null;
    return new DisplayScreeningPageResult(items, nextCursor, hasNext);
  }
}
