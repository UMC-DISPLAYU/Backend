package com.example.demo.domain.displayartwork.application.service;

import com.example.demo.domain.displayartwork.application.query.ArtworkSummaryQueryRepository;
import com.example.demo.domain.displayartwork.application.result.ArtworkSummaryResult;
import com.example.demo.domain.displayartwork.application.usecase.GetArtworkSummariesUseCase;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetArtworkSummariesService implements GetArtworkSummariesUseCase {

  private final ArtworkSummaryQueryRepository queryRepository;

  public GetArtworkSummariesService(ArtworkSummaryQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ArtworkSummaryResult> getArtworkSummaries(List<Long> displayArtworkIds) {
    if (displayArtworkIds.isEmpty()) {
      return List.of();
    }
    return queryRepository.findByDisplayArtworkIdIn(displayArtworkIds).stream()
        .map(ArtworkSummaryResult::from)
        .toList();
  }
}
