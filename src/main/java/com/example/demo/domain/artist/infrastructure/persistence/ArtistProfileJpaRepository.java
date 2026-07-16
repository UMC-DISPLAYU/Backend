package com.example.demo.domain.artist.infrastructure.persistence;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.user.domain.aggregate.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistProfileJpaRepository extends JpaRepository<ArtistProfile, Long> {

  Optional<ArtistProfile> findByUser(User user);

  Optional<ArtistProfile> findByUserId(Long userId);

  boolean existsByArtistName(String artistName);
}
