package com.example.demo.domain.display.application.query;

import java.time.LocalDate;
import java.util.List;

public interface PopularDisplayQueryRepository {
  List<ClosingSoonDisplayQueryResult> findPopularDisplays(LocalDate today);
}
