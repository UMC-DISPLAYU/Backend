package com.example.demo.domain.displayartwork.application.usecase;

import com.example.demo.domain.displayartwork.application.result.ArtworkSummaryResult;
import java.util.List;

public interface GetArtworkSummariesUseCase {

  List<ArtworkSummaryResult> getArtworkSummaries(List<Long> displayArtworkIds);
}
