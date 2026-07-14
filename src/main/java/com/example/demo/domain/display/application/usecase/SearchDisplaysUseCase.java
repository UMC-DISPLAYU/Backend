package com.example.demo.domain.display.application.usecase;

import com.example.demo.domain.display.application.query.SearchDisplayQuery;
import com.example.demo.domain.display.application.result.SearchDisplayResult;

public interface SearchDisplaysUseCase {

  SearchDisplayResult searchDisplays(SearchDisplayQuery query);
}
