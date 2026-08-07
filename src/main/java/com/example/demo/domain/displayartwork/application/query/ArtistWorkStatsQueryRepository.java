package com.example.demo.domain.displayartwork.application.query;

import java.util.List;

public interface ArtistWorkStatsQueryRepository {

  List<ArtistWorkStatsQueryResult> findByUserIdIn(List<Long> userIds);
}
