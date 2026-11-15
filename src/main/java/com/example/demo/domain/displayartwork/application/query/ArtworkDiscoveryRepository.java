package com.example.demo.domain.displayartwork.application.query;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import java.time.LocalDate;
import java.util.List;

public interface ArtworkDiscoveryRepository {
  List<DisplayArtwork> search(SearchArtworksQuery query, LocalDate today);
}
