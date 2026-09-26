package com.example.demo.domain.artist.domain.repository;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import java.util.Optional;

public interface ArtistProfileRepository {

  ArtistProfile save(ArtistProfile artistProfile);

  Optional<ArtistProfile> findByUserId(Long userId);

  boolean existsByArtistName(String artistName);

  void flush();
}
