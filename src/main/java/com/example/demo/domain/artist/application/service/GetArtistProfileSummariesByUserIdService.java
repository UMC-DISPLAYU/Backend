package com.example.demo.domain.artist.application.service;

import com.example.demo.domain.artist.application.query.ArtistProfileSummaryQueryRepository;
import com.example.demo.domain.artist.application.result.ArtistProfileSummaryResult;
import com.example.demo.domain.artist.application.usecase.GetArtistProfileSummariesByUserIdUseCase;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetArtistProfileSummariesByUserIdService
    implements GetArtistProfileSummariesByUserIdUseCase {

  private final ArtistProfileSummaryQueryRepository queryRepository;

  public GetArtistProfileSummariesByUserIdService(
      ArtistProfileSummaryQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ArtistProfileSummaryResult> getArtistProfileSummariesByUserId(List<Long> userIds) {
    if (userIds.isEmpty()) {
      return List.of();
    }
    return queryRepository.findByUserIdIn(userIds).stream()
        .map(ArtistProfileSummaryResult::from)
        .toList();
  }
}
