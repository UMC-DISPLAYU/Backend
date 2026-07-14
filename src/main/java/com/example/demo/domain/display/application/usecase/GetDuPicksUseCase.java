package com.example.demo.domain.display.application.usecase;

import com.example.demo.domain.display.application.query.DuPickQuery;
import com.example.demo.domain.display.application.result.DuPickResult;

public interface GetDuPicksUseCase {

  DuPickResult getDuPicks(DuPickQuery query);
}
