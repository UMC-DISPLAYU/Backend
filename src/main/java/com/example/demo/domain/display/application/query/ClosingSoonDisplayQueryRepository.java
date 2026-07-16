package com.example.demo.domain.display.application.query;

import java.time.LocalDate;
import java.util.List;

public interface ClosingSoonDisplayQueryRepository {

  List<ClosingSoonDisplayQueryResult> findClosingSoonDisplays(
      ClosingSoonDisplayQuery query, LocalDate today, int limit);
}
