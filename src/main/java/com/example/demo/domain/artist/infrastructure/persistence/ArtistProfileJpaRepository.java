package com.example.demo.domain.artist.infrastructure.persistence;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.user.domain.aggregate.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistProfileJpaRepository
        extends JpaRepository<ArtistProfile, Long> {

    Optional<ArtistProfile> findByUser(User user);

    boolean existsByArtistName(String artistName);
}
