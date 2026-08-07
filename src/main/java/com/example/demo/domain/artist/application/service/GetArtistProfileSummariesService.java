package com.example.demo.domain.artist.application.service;

import com.example.demo.domain.artist.application.query.ArtistProfileSummaryQueryRepository;
import com.example.demo.domain.artist.application.result.ArtistProfileSummaryResult;
import com.example.demo.domain.artist.application.usecase.GetArtistProfileSummariesUseCase;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetArtistProfileSummariesService implements GetArtistProfileSummariesUseCase {

  private final ArtistProfileSummaryQueryRepository queryRepository;

  public GetArtistProfileSummariesService(ArtistProfileSummaryQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ArtistProfileSummaryResult> getArtistProfileSummaries(List<Long> artistProfileIds) {
    if (artistProfileIds.isEmpty()) {
      return List.of();
    }
    return queryRepository.findByArtistProfileIdIn(artistProfileIds).stream()
        .map(ArtistProfileSummaryResult::from)
        .toList();
  }
}
