package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.cache.DisplayCacheNames;
import com.example.demo.domain.display.application.query.DuPickQuery;
import com.example.demo.domain.display.application.query.DuPickQueryRepository;
import com.example.demo.domain.display.application.query.DuPickQueryResult;
import com.example.demo.domain.display.application.result.DuPickResult;
import com.example.demo.domain.display.application.result.DuPickResult.DuPickItemResult;
import com.example.demo.domain.display.application.result.DuPickResult.PaginationResult;
import com.example.demo.domain.display.application.usecase.GetDuPicksUseCase;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDuPicksService implements GetDuPicksUseCase {

  private final DuPickQueryRepository queryRepository;

  public GetDuPicksService(DuPickQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Cacheable(
      cacheNames = DisplayCacheNames.DU_PICKS,
      key = "{@displayListCacheVersion.current(), #query}")
  @Transactional(readOnly = true)
  public DuPickResult getDuPicks(DuPickQuery query) {
    List<DuPickQueryResult> queryResults = queryRepository.findDuPicks(query, query.size() + 1);
    boolean hasNext = queryResults.size() > query.size();
    List<DuPickQueryResult> pageResults =
        hasNext ? queryResults.subList(0, query.size()) : queryResults;
    List<DuPickItemResult> duPicks = pageResults.stream().map(DuPickItemResult::from).toList();
    Long nextCursor = hasNext && !duPicks.isEmpty() ? duPicks.getLast().duPickId() : null;

    return new DuPickResult(duPicks, new PaginationResult(nextCursor, query.size(), hasNext));
  }
}
