package com.example.demo.domain.displayartwork.application.query;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import java.time.LocalDate;
import java.util.List;

public interface ArtworkDiscoveryRepository {
  List<DisplayArtwork> search(SearchArtworksQuery query, LocalDate today);

  List<DisplayArtwork> recommend(List<ArtworkType> fields, String school);
}
