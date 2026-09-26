package com.example.demo.domain.artist.infrastructure.persistence;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistProfileJpaRepository extends JpaRepository<ArtistProfile, Long> {

  Optional<ArtistProfile> findByUserId(Long userId);

  // User 엔티티 의존 제거

  boolean existsByArtistName(String artistName);
}
