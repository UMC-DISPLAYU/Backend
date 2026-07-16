package com.example.demo.domain.artist.domain.repository;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.user.domain.aggregate.User;

import java.util.Optional;

public interface ArtistProfileRepository {

    ArtistProfile save(ArtistProfile artistProfile);

    Optional<ArtistProfile> findByUser(User user);

    boolean existsByArtistName(String artistName);
}
