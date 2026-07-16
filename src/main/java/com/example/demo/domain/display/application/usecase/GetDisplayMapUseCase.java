package com.example.demo.domain.display.application.usecase;

import com.example.demo.domain.display.application.query.DisplayMapQuery;
import com.example.demo.domain.display.application.result.DisplayMapResult;

public interface GetDisplayMapUseCase {

  DisplayMapResult getDisplayMap(DisplayMapQuery query);
}
