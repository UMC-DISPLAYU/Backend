package com.example.demo.domain.display.application.query;

import java.util.List;

public interface DisplayScreeningQueryRepository {

  List<DisplayScreeningSummaryQueryResult> search(DisplayScreeningSearchQuery query, int limit);
}
