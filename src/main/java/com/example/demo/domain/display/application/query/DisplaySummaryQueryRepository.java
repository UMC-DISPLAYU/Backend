package com.example.demo.domain.display.application.query;

import java.util.List;

public interface DisplaySummaryQueryRepository {

  List<DisplaySummaryQueryResult> findByDisplayIdIn(List<Long> displayIds);
}
