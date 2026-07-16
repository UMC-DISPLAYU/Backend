package com.example.demo.domain.display.application.query;

import java.time.LocalDate;
import java.util.List;

public interface SearchDisplayQueryRepository {

  List<SearchDisplayQueryResult> searchDisplays(
      SearchDisplayQuery query, LocalDate today, int limit);
}
