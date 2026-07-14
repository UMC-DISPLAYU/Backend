package com.example.demo.domain.display.application.query;

import java.util.List;

public interface DisplayMapQueryRepository {

  List<DisplayMapQueryResult> findMarkers(DisplayMapQuery query, int limit);
}
