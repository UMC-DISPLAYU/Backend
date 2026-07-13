package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.query.SearchDisplayQuery;
import com.example.demo.domain.display.application.query.SearchDisplayQueryRepository;
import com.example.demo.domain.display.application.query.SearchDisplayQueryResult;
import com.example.demo.domain.display.application.result.SearchDisplayResult;
import com.example.demo.domain.display.application.result.SearchDisplayResult.ExhibitionResult;
import com.example.demo.domain.display.application.result.SearchDisplayResult.PaginationResult;
import com.example.demo.domain.display.application.usecase.SearchDisplaysUseCase;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchDisplaysService implements SearchDisplaysUseCase {

  private final SearchDisplayQueryRepository queryRepository;

  public SearchDisplaysService(SearchDisplayQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public SearchDisplayResult searchDisplays(SearchDisplayQuery query) {
    LocalDate today = LocalDate.now();
    List<SearchDisplayQueryResult> queryResults =
        queryRepository.searchDisplays(query, today, query.size() + 1);
    boolean hasNext = queryResults.size() > query.size();
    List<SearchDisplayQueryResult> pageResults =
        hasNext ? queryResults.subList(0, query.size()) : queryResults;
    List<ExhibitionResult> exhibitions =
        pageResults.stream().map(queryResult -> ExhibitionResult.from(queryResult, today)).toList();
    Long nextCursor = hasNext && !exhibitions.isEmpty() ? exhibitions.getLast().displayId() : null;

    return new SearchDisplayResult(
        exhibitions, new PaginationResult(nextCursor, query.size(), hasNext));
  }
}
