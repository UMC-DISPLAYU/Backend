package com.example.demo.domain.artist.application.query;

import java.util.List;

public interface ArtistProfileSummaryQueryRepository {

  List<ArtistProfileSummaryQueryResult> findByArtistProfileIdIn(List<Long> artistProfileIds);

  List<ArtistProfileSummaryQueryResult> findByUserIdIn(List<Long> userIds);
}
