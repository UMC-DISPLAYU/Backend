package com.example.demo.domain.display.application.usecase;

import com.example.demo.domain.display.application.result.DisplaySummaryResult;
import java.util.List;

public interface GetDisplaySummariesUseCase {

  List<DisplaySummaryResult> getDisplaySummaries(List<Long> displayIds);
}
