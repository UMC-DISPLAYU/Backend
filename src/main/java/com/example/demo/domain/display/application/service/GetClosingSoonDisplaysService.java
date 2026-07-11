package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQuery;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQuery.Cursor;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryRepository;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult;
import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult.ExhibitionResult;
import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult.PaginationResult;
import com.example.demo.domain.display.application.usecase.GetClosingSoonDisplaysUseCase;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetClosingSoonDisplaysService implements GetClosingSoonDisplaysUseCase {

  private final ClosingSoonDisplayQueryRepository queryRepository;

  public GetClosingSoonDisplaysService(ClosingSoonDisplayQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public ClosingSoonDisplayResult getClosingSoonDisplays(ClosingSoonDisplayQuery query) {
    LocalDate today = LocalDate.now();
    List<ClosingSoonDisplayQueryResult> queryResults =
        queryRepository.findClosingSoonDisplays(query, today, query.size() + 1);
    boolean hasNext = queryResults.size() > query.size();
    List<ClosingSoonDisplayQueryResult> pageResults =
        hasNext ? queryResults.subList(0, query.size()) : queryResults;
    List<ExhibitionResult> exhibitions =
        pageResults.stream().map(queryResult -> ExhibitionResult.from(queryResult, today)).toList();
    String nextCursor =
        hasNext && !exhibitions.isEmpty()
            ? new Cursor(exhibitions.getLast().endedAt(), exhibitions.getLast().displayId()).value()
            : null;

    return new ClosingSoonDisplayResult(
        exhibitions, new PaginationResult(nextCursor, query.size(), hasNext));
  }
}
