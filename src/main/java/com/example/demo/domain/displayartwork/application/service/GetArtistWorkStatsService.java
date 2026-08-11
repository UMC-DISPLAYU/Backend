package com.example.demo.domain.displayartwork.application.service;

import com.example.demo.domain.displayartwork.application.query.ArtistWorkStatsQueryRepository;
import com.example.demo.domain.displayartwork.application.result.ArtistWorkStatsResult;
import com.example.demo.domain.displayartwork.application.usecase.GetArtistWorkStatsUseCase;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetArtistWorkStatsService implements GetArtistWorkStatsUseCase {

  private final ArtistWorkStatsQueryRepository queryRepository;

  public GetArtistWorkStatsService(ArtistWorkStatsQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ArtistWorkStatsResult> getArtistWorkStats(List<Long> userIds) {
    if (userIds.isEmpty()) {
      return List.of();
    }
    return queryRepository.findByUserIdIn(userIds).stream()
        .map(ArtistWorkStatsResult::from)
        .toList();
  }
}
