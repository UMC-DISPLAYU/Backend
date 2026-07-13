package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.query.DisplayMapQuery;
import com.example.demo.domain.display.application.query.DisplayMapQueryRepository;
import com.example.demo.domain.display.application.query.DisplayMapQueryResult;
import com.example.demo.domain.display.application.result.DisplayMapResult;
import com.example.demo.domain.display.application.result.DisplayMapResult.MarkerResult;
import com.example.demo.domain.display.application.result.DisplayMapResult.PaginationResult;
import com.example.demo.domain.display.application.usecase.GetDisplayMapUseCase;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayMapService implements GetDisplayMapUseCase {

  private final DisplayMapQueryRepository displayMapQueryRepository;

  public GetDisplayMapService(DisplayMapQueryRepository displayMapQueryRepository) {
    this.displayMapQueryRepository = displayMapQueryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public DisplayMapResult getDisplayMap(DisplayMapQuery query) {
    List<DisplayMapQueryResult> queryResults =
        displayMapQueryRepository.findMarkers(query, query.size() + 1);

    boolean hasNext = queryResults.size() > query.size();
    List<DisplayMapQueryResult> pageResults =
        hasNext ? queryResults.subList(0, query.size()) : queryResults;
    List<MarkerResult> markers = pageResults.stream().map(MarkerResult::from).toList();
    Long nextCursor = hasNext && !markers.isEmpty() ? markers.getLast().displayId() : null;

    return new DisplayMapResult(markers, new PaginationResult(nextCursor, query.size(), hasNext));
  }
}
