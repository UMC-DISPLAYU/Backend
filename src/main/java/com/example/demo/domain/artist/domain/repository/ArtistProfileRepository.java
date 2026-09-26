package com.example.demo.domain.artist.domain.repository;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import java.util.Optional;

public interface ArtistProfileRepository {

  ArtistProfile save(ArtistProfile artistProfile);

  Optional<ArtistProfile> findByUserId(Long userId);

  // 도메인 간 결합 제거를 위해 User->Long으로 userId를 받는다.

  boolean existsByArtistName(String artistName);

  void flush();
}
