package com.example.demo.domain.displayartwork.application.usecase;

import com.example.demo.domain.displayartwork.application.result.ArtistWorkStatsResult;
import java.util.List;

public interface GetArtistWorkStatsUseCase {

  List<ArtistWorkStatsResult> getArtistWorkStats(List<Long> userIds);
}
