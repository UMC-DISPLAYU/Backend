package com.example.demo.domain.display.application.query;

import java.util.List;

public interface DuPickQueryRepository {

  List<DuPickQueryResult> findDuPicks(DuPickQuery query, int limit);
}
