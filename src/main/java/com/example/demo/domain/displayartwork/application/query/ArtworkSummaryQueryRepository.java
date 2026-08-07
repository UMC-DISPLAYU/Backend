package com.example.demo.domain.displayartwork.application.query;

import java.util.List;

public interface ArtworkSummaryQueryRepository {

  List<ArtworkSummaryQueryResult> findByDisplayArtworkIdIn(List<Long> displayArtworkIds);
}
